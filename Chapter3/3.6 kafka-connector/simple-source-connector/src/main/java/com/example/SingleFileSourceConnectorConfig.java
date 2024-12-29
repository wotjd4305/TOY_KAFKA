package com.example;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;

import java.util.Map;

public class SingleFileSourceConnectorConfig extends AbstractConfig {

    // 읽을 파일 정보
    public static final String DIR_FILE_NAME = "file";
    private static final String DIR_FILE_NAME_DEFAULT_VALUE = "/tmp/kafka.txt";
    private static final String DIR_FILE_NAME_DOC = "읽을 파일 경로와 이름";

    // 어느 토픽으로 보낼지
    public static final String TOPIC_NAME = "topic";
    private static final String TOPIC_DEFAULT_VALUE = "test";
    private static final String TOPIC_DOC = "보낼 토픽명";

    // 이름, 설명, 기본값, 중요도 지정
    // 중요도는 명확한 기준이 없어, 사용자로 하여금 중요하다는 명시적으로 표시를 위함.
    public static ConfigDef CONFIG = new ConfigDef().define(DIR_FILE_NAME,
                                                    Type.STRING,
                                                    DIR_FILE_NAME_DEFAULT_VALUE,
                                                    Importance.HIGH,
                                                    DIR_FILE_NAME_DOC)
                                                    .define(TOPIC_NAME,
                                                            Type.STRING,
                                                            TOPIC_DEFAULT_VALUE,
                                                            Importance.HIGH,
                                                            TOPIC_DOC);

    public SingleFileSourceConnectorConfig(Map<String, String> props) {
        super(CONFIG, props);
    }
}
