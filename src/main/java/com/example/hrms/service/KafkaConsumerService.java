package com.example.hrms.service;

import com.example.hrms.dto.HrmsEvent;
import com.example.hrms.entity.EventLog;
import com.example.hrms.repository.EventLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final EventLogRepository eventLogRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "hrms.events", groupId = "hrms-service")
    public void consumeEvent(HrmsEvent event) {
        try {
            String eventDataJson = objectMapper.writeValueAsString(event.eventData());
            
            EventLog eventLog = new EventLog();
            eventLog.setEventType(event.eventType());
            eventLog.setEventData(eventDataJson);
            eventLog.setDescription(event.description());
            eventLog.setEventTimestamp(event.timestamp());
            
            eventLogRepository.save(eventLog);
            
        } catch (JsonProcessingException e) {
        } catch (Exception e) {
        }
    }
}
