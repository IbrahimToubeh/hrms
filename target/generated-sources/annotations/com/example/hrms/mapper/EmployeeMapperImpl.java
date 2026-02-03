package com.example.hrms.mapper;

import com.example.hrms.dto.CreateEmployeeRequestDTO;
import com.example.hrms.dto.EmployeeResponseDTO;
import com.example.hrms.dto.UpdateEmployeeRequestDTO;
import com.example.hrms.entity.Employee;
import com.example.hrms.entity.EmploymentStatus;
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
public class EmployeeMapperImpl implements EmployeeMapper {

    @Override
    public EmployeeResponseDTO toDto(Employee employee) {
        if ( employee == null ) {
            return null;
        }

        Long id = null;
        Long userId = null;
        String firstName = null;
        String lastName = null;
        String email = null;
        String phoneNumber = null;
        String department = null;
        String position = null;
        LocalDate dateOfBirth = null;
        LocalDate joiningDate = null;
        String address = null;
        String status = null;
        LocalDateTime createdAt = null;

        id = employee.getId();
        userId = employee.getUserId();
        firstName = employee.getFirstName();
        lastName = employee.getLastName();
        email = employee.getEmail();
        phoneNumber = employee.getPhoneNumber();
        department = employee.getDepartment();
        position = employee.getPosition();
        dateOfBirth = employee.getDateOfBirth();
        joiningDate = employee.getJoiningDate();
        address = employee.getAddress();
        if ( employee.getStatus() != null ) {
            status = employee.getStatus().name();
        }
        createdAt = employee.getCreatedAt();

        String employeeCode = null;

        EmployeeResponseDTO employeeResponseDTO = new EmployeeResponseDTO( id, userId, employeeCode, firstName, lastName, email, phoneNumber, department, position, dateOfBirth, joiningDate, address, status, createdAt );

        return employeeResponseDTO;
    }

    @Override
    public Employee toEntity(CreateEmployeeRequestDTO request) {
        if ( request == null ) {
            return null;
        }

        Employee.EmployeeBuilder employee = Employee.builder();

        employee.address( request.address() );
        employee.dateOfBirth( request.dateOfBirth() );
        employee.department( request.department() );
        employee.email( request.email() );
        employee.firstName( request.firstName() );
        employee.joiningDate( request.joiningDate() );
        employee.lastName( request.lastName() );
        employee.phoneNumber( request.phoneNumber() );
        employee.position( request.position() );
        employee.userId( request.userId() );

        employee.status( EmploymentStatus.ACTIVE );

        return employee.build();
    }

    @Override
    public void updateEmployeeFromDto(UpdateEmployeeRequestDTO request, Employee employee) {
        if ( request == null ) {
            return;
        }

        if ( request.address() != null ) {
            employee.setAddress( request.address() );
        }
        if ( request.phoneNumber() != null ) {
            employee.setPhoneNumber( request.phoneNumber() );
        }
    }
}
