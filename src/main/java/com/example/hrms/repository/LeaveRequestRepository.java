package com.example.hrms.repository;

import com.example.hrms.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByLeaveRequesterId(Long leaveRequesterId);

    List<LeaveRequest> findByReporterId(Long reporterId);
}
