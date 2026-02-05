package com.example.hrms.controller;

import com.example.hrms.dto.ApiResponse;
import com.example.hrms.dto.PageResponse;
import com.example.hrms.dto.CreateLeaveRequestDTO;
import com.example.hrms.dto.LeaveResponseDTO;
import com.example.hrms.dto.ReviewLeaveRequestDTO;
import com.example.hrms.service.LeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hrms/leaves")
@RequiredArgsConstructor
public class LeaveManagementController {

    private final LeaveService leaveService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<LeaveResponseDTO>> requestLeave(
            @Valid @RequestBody CreateLeaveRequestDTO request) {
        LeaveResponseDTO leaveRequest = leaveService.requestLeave(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Leave requested successfully", leaveRequest));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<LeaveResponseDTO>>> getMyLeaves(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<LeaveResponseDTO> leaves = leaveService.getMyLeaves(page, size);
        return ResponseEntity.ok(ApiResponse.success("My leaves retrieved successfully", leaves));
    }

    @GetMapping("/reported")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<LeaveResponseDTO>>> getReportedLeaves(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<LeaveResponseDTO> leaves = leaveService.getReportedLeaves(page, size);
        return ResponseEntity.ok(ApiResponse.success("Reported leaves retrieved successfully", leaves));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<LeaveResponseDTO>>> getAllLeaves(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<LeaveResponseDTO> leaves = leaveService.getAllLeaves(page, size);
        return ResponseEntity.ok(ApiResponse.success("All leaves retrieved successfully", leaves));
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LeaveResponseDTO>> approveLeave(
            @PathVariable Long id,
            @RequestBody(required = false) ReviewLeaveRequestDTO reviewDTO) {
        LeaveResponseDTO leaveRequest = leaveService.approveLeave(id, reviewDTO);
        return ResponseEntity.ok(ApiResponse.success("Leave approved successfully", leaveRequest));
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LeaveResponseDTO>> rejectLeave(
            @PathVariable Long id,
            @RequestBody(required = false) ReviewLeaveRequestDTO reviewDTO) {
        LeaveResponseDTO leaveRequest = leaveService.rejectLeave(id, reviewDTO);
        return ResponseEntity.ok(ApiResponse.success("Leave rejected successfully", leaveRequest));
    }
}
