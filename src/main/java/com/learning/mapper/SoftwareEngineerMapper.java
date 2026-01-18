package com.learning.mapper;

import com.learning.SoftwareEngineer;
import com.learning.dto.SoftwareEngineerRequestDto;
import com.learning.dto.SoftwareEngineerResponseDto;
import org.springframework.stereotype.Component;

@Component
public class SoftwareEngineerMapper {

    public SoftwareEngineer toEntity(SoftwareEngineerRequestDto dto) {
        SoftwareEngineer entity = new SoftwareEngineer();
        entity.setName(dto.getName());
        entity.setTechStack(dto.getTechStack());
        return entity;
    }

    public SoftwareEngineerResponseDto toResponseDto(SoftwareEngineer entity) {
        return new SoftwareEngineerResponseDto(
                entity.getId(),
                entity.getName(),
                entity.getTechStack()
        );
    }
}
