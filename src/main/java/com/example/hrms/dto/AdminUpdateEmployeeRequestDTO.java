package com.example.hrms.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Size;

public record AdminUpdateEmployeeRequestDTO(
        @Size(max = 50) String firstName,
        @Size(max = 50) String lastName,
        @Size(max = 100) String email,
        @Size(max = 20) String phoneNumber,
        @Size(max = 50) String department,
        @Size(max = 50) String position,
        LocalDate dateOfBirth,
        LocalDate joiningDate,
        @Size(max = 255) String address,
        String status) {
}
