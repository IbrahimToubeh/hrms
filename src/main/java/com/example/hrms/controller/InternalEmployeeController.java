package com.example.hrms.controller;

import com.example.hrms.dto.InternalEmployeeCreateRequest;
import com.example.hrms.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal/employees")
@RequiredArgsConstructor
public class InternalEmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createShellEmployee(@RequestBody InternalEmployeeCreateRequest request) {
        employeeService.createShellEmployee(request);
    }
    @PatchMapping("/{userId}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateEmployeeStatus(@PathVariable Long userId, @RequestParam String status) {
        employeeService.updateEmployeeStatus(userId, com.example.hrms.entity.EmploymentStatus.valueOf(status));
    }

    @PatchMapping("/{userId}/email")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateEmployeeEmail(@PathVariable Long userId, @RequestParam String email) {
        employeeService.updateEmployeeEmailInternal(userId, email);
    }
}
