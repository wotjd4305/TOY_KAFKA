package com.example;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ConsumerWorker implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(ConsumerWorker.class);

    private final Properties prop;
    private final String topic;
    private final String threadName;
    private KafkaConsumer<String, String> consumer;

    ConsumerWorker(Properties prop, String topic, int number) {
        this.prop = prop;
        this.topic = topic;
        this.threadName = "consumer-thread-" + number; // 스레드 구별한 스레드 번호
    }

    @Override
    public void run() {
        consumer = new KafkaConsumer<>(prop); // 스레드 세이프하지 않아 스레드별로 Kafka Consumer 인스턴스 별개 운영
        consumer.subscribe(Arrays.asList(topic));
        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                for (ConsumerRecord<String, String> record : records) {
                    logger.info("{}", record);
                }
                consumer.commitSync();
            }
        } catch (WakeupException e) {
            System.out.println(threadName + " trigger WakeupException");
        } finally {
            consumer.commitSync();
            consumer.close();
        }
    }

    public void shutdown() {
        consumer.wakeup();
    }
}