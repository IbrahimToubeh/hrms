package com.example.hrms.service;

import com.example.hrms.dto.HrmsEvent;
import com.example.hrms.entity.EventLog;
import com.example.hrms.repository.EventLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final EventLogRepository eventLogRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "hrms.events", groupId = "hrms-service")
    public void consumeEvent(HrmsEvent event) {
        log.info("Received Kafka event: {}", event.eventType());
        try {
            String eventDataJson = objectMapper.writeValueAsString(event.eventData());
            
            EventLog eventLog = new EventLog();
            eventLog.setEventType(event.eventType());
            eventLog.setEventData(eventDataJson);
            eventLog.setDescription(event.description());
            eventLog.setEventTimestamp(event.timestamp());
            
            eventLogRepository.save(eventLog);
            log.info("Successfully logged event: {}", event.eventType());
            
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event data for event: {}", event.eventType(), e);
        } catch (Exception e) {
            log.error("Error processing Kafka event: {}", event.eventType(), e);
        }
    }
}
