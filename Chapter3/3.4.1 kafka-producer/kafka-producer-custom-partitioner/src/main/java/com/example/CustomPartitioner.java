package com.example;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.InvalidRecordException;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.utils.Utils;

import java.util.List;
import java.util.Map;

public class CustomPartitioner  implements Partitioner {

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes,
                         Cluster cluster) {

        if (keyBytes == null) { // 메시지 키 미 설정 시, 오류로 간주
            throw new InvalidRecordException("Need message key");
        }
        if (((String)key).equals("PangyoKey"))
            return 0;

        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic); // 특정 키값이 아니면 해쉬처리
        int numPartitions = partitions.size();
        return Utils.toPositive(Utils.murmur2(keyBytes)) % numPartitions;
    }


    @Override
    public void configure(Map<String, ?> configs) {}

    @Override
    public void close() {}
}
