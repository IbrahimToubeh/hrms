package com.example.hrms.service;

import com.example.hrms.dto.HrmsEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendEvent(HrmsEvent event) {
        log.info("Sending Kafka event: {}", event);
        kafkaTemplate.send("hrms.events", event);
    }
}
