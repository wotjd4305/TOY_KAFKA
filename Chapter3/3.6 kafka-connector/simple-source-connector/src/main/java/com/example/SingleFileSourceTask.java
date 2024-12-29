package com.example;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.apache.kafka.connect.storage.OffsetStorageReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SingleFileSourceTask extends SourceTask {
    private Logger logger = LoggerFactory.getLogger(SingleFileSourceTask.class);

    public final String FILENAME_FIELD = "filename"; // 파일을 읽은 지점을 정의
    public final String POSITION_FIELD = "position";

    private Map<String, String> fileNamePartition; // 데이터를 저장하고 읽을 때
    private Map<String, Object> offset;
    private String topic;
    private String file;
    private long position = -1; // 읽은 파일의 위치, 마지막으로 읽으 위치를 저장하여 중복 적재 방지


    @Override
    public String version() {
        return "1.0";
    }

    @Override
    public void start(Map<String, String> props) {
        try {
            // Init variables
            SingleFileSourceConnectorConfig config = new SingleFileSourceConnectorConfig(props); // 설정 값 사용(토픽이름, 읽을 파일 이름)
            topic = config.getString(SingleFileSourceConnectorConfig.TOPIC_NAME);
            file = config.getString(SingleFileSourceConnectorConfig.DIR_FILE_NAME);
            fileNamePartition = Collections.singletonMap(FILENAME_FIELD, file);
            offset = context.offsetStorageReader().offset(fileNamePartition); 

            // Get file offset from offsetStorageReader
            if (offset != null) { // 한번이라도 커넥터로 처리가 되었다.
                Object lastReadFileOffset = offset.get(POSITION_FIELD);
                if (lastReadFileOffset != null) {
                    position = (Long) lastReadFileOffset; // 커넥터 재시작되더라도 데이터 중복/유실 방지
                }
            } else { // 읽을 데이터가 없다
                position = 0;
            }

        } catch (Exception e) {
            throw new ConnectException(e.getMessage(), e);
        }
    }

    @Override
    public List<SourceRecord> poll() { // 태스크 시작 이후, 계속해서 데이터를 가져와서 처리
        List<SourceRecord> results = new ArrayList<>();
        try {
            Thread.sleep(1000);

            List<String> lines = getLines(position);

            if (lines.size() > 0) {
                lines.forEach(line -> { 
                    Map<String, Long> sourceOffset = Collections.singletonMap(POSITION_FIELD, ++position);
                    SourceRecord sourceRecord = new SourceRecord(fileNamePartition, sourceOffset, topic, Schema.STRING_SCHEMA, line);
                    results.add(sourceRecord);
                });
            }
            return results;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ConnectException(e.getMessage(), e);
        }
    }

    private List<String> getLines(long readLine) throws Exception { // 마지막으로 읽은 지점 이후부터 끝까지
        BufferedReader reader = Files.newBufferedReader(Paths.get(file));
        return reader.lines().skip(readLine).collect(Collectors.toList());
    }

    @Override
    public void stop() {
    }
}


