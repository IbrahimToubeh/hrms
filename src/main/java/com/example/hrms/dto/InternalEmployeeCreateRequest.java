package com.example.hrms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record InternalEmployeeCreateRequest(
    @NotNull Long userId,
    @Email String email,
    String firstName,
    String lastName
) {}
