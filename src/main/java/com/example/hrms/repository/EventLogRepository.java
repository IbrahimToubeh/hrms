package com.example.hrms.repository;

import com.example.hrms.entity.EventLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventLogRepository extends JpaRepository<EventLog, Long> {
    List<EventLog> findByEventTypeOrderByReceivedAtDesc(String eventType);
    List<EventLog> findAllByOrderByReceivedAtDesc();
}
