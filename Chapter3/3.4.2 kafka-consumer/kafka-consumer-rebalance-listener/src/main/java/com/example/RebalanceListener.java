package com.example;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class RebalanceListener implements ConsumerRebalanceListener { // 리밸런싱을 감지하는 인터페이스
    private final static Logger logger = LoggerFactory.getLogger(RebalanceListener.class);

    public void onPartitionsAssigned(Collection<TopicPartition> partitions) { // 리밸런싱 끝난뒤에 파티션 할당 완료되면 호출
        logger.warn("Partitions are assigned : " + partitions.toString());

    }

    public void onPartitionsRevoked(Collection<TopicPartition> partitions) { // 리밸런스 시작되기 직전에 호출
        logger.warn("Partitions are revoked : " + partitions.toString());
    }
}