package com.example.personmanagement.mapper;

import com.example.personmanagement.model.position.CreatePositionCommand;
import com.example.personmanagement.model.position.JobPosition;
import com.example.personmanagement.model.position.PositionDto;

public class PositionMapper {

    public static PositionDto toDto(JobPosition jobPosition) {
        if (jobPosition == null) {
            return null;
        }

        PositionDto dto = new PositionDto();
        dto.setId(jobPosition.getId());
        dto.setPositionName(jobPosition.getPositionName());
        dto.setSalary(jobPosition.getSalary());
        dto.setStartDate(jobPosition.getStartDate());
        dto.setEndDate(jobPosition.getEndDate());
        if (jobPosition.getEmployee() != null) {
            dto.setEmployeeId(jobPosition.getEmployee().getId());
        }
        return dto;
    }

    public static JobPosition fromCreateCommand(CreatePositionCommand command) {
        JobPosition jobPosition = new JobPosition();
        jobPosition.setPositionName(command.getPositionName());
        jobPosition.setStartDate(command.getStartDate());
        jobPosition.setEndDate(command.getEndDate());
        jobPosition.setSalary(command.getSalary());

        return jobPosition;
    }
}

