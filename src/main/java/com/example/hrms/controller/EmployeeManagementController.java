package com.example.hrms.controller;

import com.example.hrms.dto.ApiResponse;
import com.example.hrms.dto.PageResponse;
import com.example.hrms.dto.CreateEmployeeRequestDTO;
import com.example.hrms.dto.EmployeeResponseDTO;
import com.example.hrms.dto.UpdateEmployeeRequestDTO;
import com.example.hrms.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hrms/employees")
@RequiredArgsConstructor
public class EmployeeManagementController {

    private final EmployeeService employeeService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> createEmployee(
            @Valid @RequestBody CreateEmployeeRequestDTO request) {
        EmployeeResponseDTO employee = employeeService.createEmployee(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Employee created successfully", employee));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<EmployeeResponseDTO>>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<EmployeeResponseDTO> employees = employeeService.getAllEmployees(page, size);
        return ResponseEntity.ok(ApiResponse.success("Employees retrieved successfully", employees));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> getEmployeeById(@PathVariable Long id) {
        EmployeeResponseDTO employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(ApiResponse.success("Employee retrieved successfully", employee));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody com.example.hrms.dto.AdminUpdateEmployeeRequestDTO request) {
        EmployeeResponseDTO employee = employeeService.updateEmployee(id, request);
        return ResponseEntity.ok(ApiResponse.success("Employee updated successfully", employee));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> getCurrentEmployee() {
        EmployeeResponseDTO employee = employeeService.getCurrentEmployee();
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", employee));
    }

    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> updateCurrentEmployee(
            @Valid @RequestBody UpdateEmployeeRequestDTO request) {
        EmployeeResponseDTO employee = employeeService.updateCurrentEmployee(request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", employee));
    }
}
