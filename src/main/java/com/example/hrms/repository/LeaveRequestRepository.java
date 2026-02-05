package com.example.hrms.repository;

import com.example.hrms.entity.LeaveRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    Page<LeaveRequest> findByLeaveRequesterId(Long leaveRequesterId, Pageable pageable);

    Page<LeaveRequest> findByReporterId(Long reporterId, Pageable pageable);
}
