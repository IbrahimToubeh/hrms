package com.example.hrms.mapper;

import com.example.hrms.dto.CreateLeaveRequestDTO;
import com.example.hrms.dto.LeaveResponseDTO;
import com.example.hrms.entity.LeaveRequest;
import com.example.hrms.entity.LeaveStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-04T00:28:00+0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260128-0750, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class LeaveRequestMapperImpl implements LeaveRequestMapper {

    @Override
    public LeaveResponseDTO toDto(LeaveRequest leaveRequest) {
        if ( leaveRequest == null ) {
            return null;
        }

        Long id = null;
        Long reporterId = null;
        Long employeeId = null;
        String email = null;
        LocalDate startDate = null;
        LocalDate endDate = null;
        String leaveType = null;
        String reason = null;
        String status = null;
        Long approvedBy = null;
        LocalDateTime reviewedAt = null;
        String adminComment = null;
        LocalDateTime createdAt = null;

        id = leaveRequest.getId();
        reporterId = leaveRequest.getReporterId();
        employeeId = leaveRequest.getEmployeeId();
        email = leaveRequest.getEmail();
        startDate = leaveRequest.getStartDate();
        endDate = leaveRequest.getEndDate();
        if ( leaveRequest.getLeaveType() != null ) {
            leaveType = leaveRequest.getLeaveType().name();
        }
        reason = leaveRequest.getReason();
        if ( leaveRequest.getStatus() != null ) {
            status = leaveRequest.getStatus().name();
        }
        approvedBy = leaveRequest.getApprovedBy();
        reviewedAt = leaveRequest.getReviewedAt();
        adminComment = leaveRequest.getAdminComment();
        createdAt = leaveRequest.getCreatedAt();

        Long leaveDays = leaveRequest.getLeaveDays();

        LeaveResponseDTO leaveResponseDTO = new LeaveResponseDTO( id, reporterId, employeeId, email, startDate, endDate, leaveType, reason, status, approvedBy, reviewedAt, adminComment, createdAt, leaveDays );

        return leaveResponseDTO;
    }

    @Override
    public LeaveRequest toEntity(CreateLeaveRequestDTO request) {
        if ( request == null ) {
            return null;
        }

        LeaveRequest.LeaveRequestBuilder leaveRequest = LeaveRequest.builder();

        leaveRequest.endDate( request.endDate() );
        leaveRequest.leaveType( request.leaveType() );
        leaveRequest.reason( request.reason() );
        leaveRequest.startDate( request.startDate() );

        leaveRequest.status( LeaveStatus.PENDING );

        return leaveRequest.build();
    }
}
