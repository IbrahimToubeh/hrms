package com.example.hrms.mapper;

import com.example.hrms.dto.CreateLeaveRequestDTO;
import com.example.hrms.dto.LeaveResponseDTO;
import com.example.hrms.entity.LeaveRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LeaveRequestMapper {

    @Mapping(target = "leaveDays", expression = "java(leaveRequest.getLeaveDays())")
    LeaveResponseDTO toDto(LeaveRequest leaveRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reporterId", ignore = true)
    @Mapping(target = "leaveRequesterId", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "approvedBy", ignore = true)
    @Mapping(target = "reviewedAt", ignore = true)
    @Mapping(target = "adminComment", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    LeaveRequest toEntity(CreateLeaveRequestDTO request);
}
