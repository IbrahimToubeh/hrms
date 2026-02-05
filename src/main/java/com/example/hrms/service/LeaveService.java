package com.example.hrms.service;

import com.example.hrms.dto.CreateLeaveRequestDTO;
import com.example.hrms.dto.HrmsEvent;
import com.example.hrms.dto.LeaveResponseDTO;
import com.example.hrms.dto.ReviewLeaveRequestDTO;
import com.example.hrms.entity.Employee;
import com.example.hrms.entity.LeaveRequest;
import com.example.hrms.entity.LeaveStatus;
import com.example.hrms.exception.ResourceNotFoundException;
import com.example.hrms.mapper.LeaveRequestMapper;
import com.example.hrms.repository.EmployeeRepository;
import com.example.hrms.repository.LeaveRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveRequestMapper leaveRequestMapper;
    private final KafkaProducerService kafkaProducerService;

    @Transactional
    public LeaveResponseDTO requestLeave(CreateLeaveRequestDTO request) {
        Long reporterId = getCurrentUserId();


        Employee employee = employeeRepository.findByUserId(request.leaveRequesterId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee profile not found. Cannot request leave."));

        LeaveRequest leaveRequest = leaveRequestMapper.toEntity(request);
        leaveRequest.setReporterId(reporterId);
        leaveRequest.setLeaveRequesterId(request.leaveRequesterId());
        leaveRequest.setEmail(employee.getEmail());

        LeaveRequest savedRequest = leaveRequestRepository.save(leaveRequest);


        HrmsEvent event = new HrmsEvent(
                "LEAVE_REQUESTED",
                Map.of("requestId", savedRequest.getId(), "leaveRequesterId", employee.getId(), "type",
                        savedRequest.getLeaveType()),
                "Leave for " + employee.getFirstName() + " " + employee.getLastName() +" and requested by employee with id" + reporterId+", reason for leave is : "+request.reason());
        kafkaProducerService.sendEvent(event);

        return leaveRequestMapper.toDto(savedRequest);
    }

    public List<LeaveResponseDTO> getMyLeaves() {
        Long userId = getCurrentUserId();
        Employee employee = employeeRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee profile not found"));

        return leaveRequestRepository.findByLeaveRequesterId(employee.getId()).stream()
                .map(leaveRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<LeaveResponseDTO> getReportedLeaves() {
        Long userId = getCurrentUserId();
        
        return leaveRequestRepository.findByReporterId(userId).stream()
                .map(leaveRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<LeaveResponseDTO> getAllLeaves() {
        return leaveRequestRepository.findAll().stream()
                .map(leaveRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public LeaveResponseDTO approveLeave(Long id, ReviewLeaveRequestDTO reviewDTO) {
        return updateLeaveStatus(id, LeaveStatus.APPROVED, reviewDTO, "LEAVE_APPROVED");
    }

    @Transactional
    public LeaveResponseDTO rejectLeave(Long id, ReviewLeaveRequestDTO reviewDTO) {
        return updateLeaveStatus(id, LeaveStatus.REJECTED, reviewDTO, "LEAVE_REJECTED");
    }

    private LeaveResponseDTO updateLeaveStatus(Long id, LeaveStatus status, ReviewLeaveRequestDTO reviewDTO,
            String eventType) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found"));

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException("Leave request is already " + leaveRequest.getStatus());
        }

        leaveRequest.setStatus(status);
        leaveRequest.setApprovedBy(getCurrentUserId());
        leaveRequest.setReviewedAt(LocalDateTime.now());
        if (reviewDTO != null) {
            leaveRequest.setAdminComment(reviewDTO.comment());
        }

        LeaveRequest savedRequest = leaveRequestRepository.save(leaveRequest);

        
        HrmsEvent event = new HrmsEvent(
                eventType,
                Map.of("requestId", savedRequest.getId(), "status", status),
                "Leave request " + status + " for " + savedRequest.getEmail());
        kafkaProducerService.sendEvent(event);

        return leaveRequestMapper.toDto(savedRequest);
    }

    private Long getCurrentUserId() {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Long.parseLong(principal);
    }
}
