package com.example.hrms.service;

import com.example.hrms.dto.HrmsEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendEvent(HrmsEvent event) {
        kafkaTemplate.send("hrms.events", event);
    }
}
