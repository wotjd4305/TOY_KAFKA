package com.example;

import org.apache.kafka.streams.processor.ProcessorContext;

import org.apache.kafka.streams.processor.Processor;

public class FilterProcessor implements Processor<String, String> {

    private ProcessorContext context; // 프로세서 정보, schedule(), forward(), commit()등 메소드

    @Override
    public void init(ProcessorContext context) {
        this.context = context;
    } // 리소스 선언

    @Override
    public void process(String key, String value) {
        if (value.length() > 5) {
            context.forward(key, value); // 다음 토폴로지로 넘어감
        }
        context.commit(); // 명시적 처리
    }

    @Override
    public void close() { // 주로 리소스 해제하는 구문
    }

}