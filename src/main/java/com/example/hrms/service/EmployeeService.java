package com.example.hrms.service;

import com.example.hrms.dto.PageResponse;
import com.example.hrms.dto.CreateEmployeeRequestDTO;
import com.example.hrms.dto.EmployeeResponseDTO;
import com.example.hrms.dto.HrmsEvent;
import com.example.hrms.dto.UpdateEmployeeRequestDTO;
import com.example.hrms.entity.Employee;
import com.example.hrms.exception.ResourceNotFoundException;
import com.example.hrms.mapper.EmployeeMapper;
import com.example.hrms.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.hrms.entity.EmploymentStatus;

@Service
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final KafkaProducerService kafkaProducerService;
    private final com.example.hrms.client.ApiClient apiClient;

    @Transactional
    public void updateEmployeeEmailInternal(Long userId, String email) {
        employeeRepository.findByUserId(userId).ifPresent(employee -> {
            employee.setEmail(email);
            employeeRepository.save(employee);
        });
    }

    @Transactional
    public void updateEmployeeStatus(Long userId, EmploymentStatus status) {
        employeeRepository.findByUserId(userId).ifPresent(employee -> {
            employee.setStatus(status);
            employeeRepository.save(employee);
        });
    }

    @Transactional
    public EmployeeResponseDTO createEmployee(CreateEmployeeRequestDTO request) {
        if (employeeRepository.existsByUserId(request.userId())) {
            throw new IllegalArgumentException("Employee profile already exists for User ID: " + request.userId());
        }
        if (employeeRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Employee profile already exists for Email: " + request.email());
        }

        Employee employee = employeeMapper.toEntity(request);
        
        
        
        employee.setUserId(request.userId());

        Employee savedEmployee = employeeRepository.save(employee);

        
        HrmsEvent event = new HrmsEvent(
                "EMPLOYEE_CREATED",
                Map.of("leaveRequesterId", savedEmployee.getId(), "userId", savedEmployee.getUserId(), "email",
                        savedEmployee.getEmail()),
                "New employee created: " + savedEmployee.getFirstName() + " " + savedEmployee.getLastName());
        kafkaProducerService.sendEvent(event);

        return employeeMapper.toDto(savedEmployee);
    }

    @Transactional
    public void createShellEmployee(com.example.hrms.dto.InternalEmployeeCreateRequest request) {
        if (employeeRepository.existsByUserId(request.userId())) {
            return;
        }
        
        Employee employee = new Employee();
        employee.setUserId(request.userId());
        employee.setEmail(request.email());
        employee.setFirstName(request.firstName() != null ? request.firstName() : "Unknown");
        employee.setLastName(request.lastName() != null ? request.lastName() : "User");
        
        
        employee.setDepartment("Unassigned");
        employee.setPosition("No Position");
        employee.setStatus(com.example.hrms.entity.EmploymentStatus.ACTIVE);
        employee.setJoiningDate(java.time.LocalDate.now());
        
        employeeRepository.save(employee);
    }

    public PageResponse<EmployeeResponseDTO> getAllEmployees(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Employee> employees = employeeRepository.findAll(pageable);
        List<EmployeeResponseDTO> content = employees.getContent().stream()
                .map(employeeMapper::toDto)
                .collect(Collectors.toList());

        return PageResponse.<EmployeeResponseDTO>builder()
                .content(content)
                .pageNo(employees.getNumber())
                .pageSize(employees.getSize())
                .totalElements(employees.getTotalElements())
                .totalPages(employees.getTotalPages())
                .last(employees.isLast())
                .build();
    }

    public EmployeeResponseDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        return employeeMapper.toDto(employee);
    }

    public EmployeeResponseDTO getCurrentEmployee() {
        Long userId = getCurrentUserId();
        Employee employee = employeeRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee profile not found for current user"));
        return employeeMapper.toDto(employee);
    }

    @Transactional
    public EmployeeResponseDTO updateEmployee(Long id, com.example.hrms.dto.AdminUpdateEmployeeRequestDTO request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        if (request.firstName() != null) employee.setFirstName(request.firstName());
        if (request.lastName() != null) employee.setLastName(request.lastName());
        
        String oldEmail = employee.getEmail();
        if (request.email() != null) employee.setEmail(request.email());
        
        if (request.phoneNumber() != null) employee.setPhoneNumber(request.phoneNumber());
        if (request.department() != null) employee.setDepartment(request.department());
        if (request.position() != null) employee.setPosition(request.position());
        if (request.dateOfBirth() != null) employee.setDateOfBirth(request.dateOfBirth());
        if (request.joiningDate() != null) employee.setJoiningDate(request.joiningDate());
        if (request.address() != null) employee.setAddress(request.address());
        if (request.status() != null) employee.setStatus(EmploymentStatus.valueOf(request.status()));

        Employee updatedEmployee = employeeRepository.save(employee);
        
        if (!updatedEmployee.getEmail().equals(oldEmail) && updatedEmployee.getUserId() != null) {
            try {
                apiClient.patch("http://localhost:8080/api/internal/users/" + updatedEmployee.getUserId() + "/email?email=" + updatedEmployee.getEmail(), null, Void.class);
            } catch (Exception e) {
                log.error("Failed to sync email to Auth Service for user {}: {}", updatedEmployee.getUserId(), e.getMessage(), e);
            }
        }

        if (request.status() != null && updatedEmployee.getUserId() != null) {
             boolean enabled = updatedEmployee.getStatus() == EmploymentStatus.ACTIVE;
             try {
                apiClient.patch("http://localhost:8080/api/internal/users/" + updatedEmployee.getUserId() + "/status?enabled=" + enabled, null, Void.class);
            } catch (Exception e) {
                log.error("Failed to sync status to Auth Service for user {}: {}", updatedEmployee.getUserId(), e.getMessage(), e);
            }
        }
        
        return employeeMapper.toDto(updatedEmployee);
    }
    
    @Transactional
    public EmployeeResponseDTO updateCurrentEmployee(UpdateEmployeeRequestDTO request) {
        Long userId = getCurrentUserId();
        Employee employee = employeeRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee profile not found for current user"));

        String oldEmail = employee.getEmail();
        employeeMapper.updateEmployeeFromDto(request, employee);
        Employee updatedEmployee = employeeRepository.save(employee);
        
        if (!updatedEmployee.getEmail().equals(oldEmail)) {
            try {
                apiClient.patch("http://localhost:8080/api/internal/users/" + userId + "/email?email=" + updatedEmployee.getEmail(), null, Void.class);
            } catch (Exception e) {
                log.error("Failed to sync email to Auth Service for user {}: {}", userId, e.getMessage(), e);
            }
        }
        
        return employeeMapper.toDto(updatedEmployee);
    }

    private Long getCurrentUserId() {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Long.parseLong(principal);
    }
}
