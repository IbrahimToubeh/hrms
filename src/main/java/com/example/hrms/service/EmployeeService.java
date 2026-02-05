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

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final KafkaProducerService kafkaProducerService;

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
    public EmployeeResponseDTO updateCurrentEmployee(UpdateEmployeeRequestDTO request) {
        Long userId = getCurrentUserId();
        Employee employee = employeeRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee profile not found for current user"));

        employeeMapper.updateEmployeeFromDto(request, employee);
        Employee updatedEmployee = employeeRepository.save(employee);
        return employeeMapper.toDto(updatedEmployee);
    }

    private Long getCurrentUserId() {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Long.parseLong(principal);
    }
}
