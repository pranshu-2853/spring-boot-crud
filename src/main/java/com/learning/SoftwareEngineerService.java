package com.learning;

import com.learning.exception.DuplicateResourceException;
import com.learning.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import com.learning.dto.SoftwareEngineerRequestDto;
import com.learning.dto.SoftwareEngineerResponseDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SoftwareEngineerService {
    private final SoftwareEngineerRepository softwareEngineerRepository;

    public SoftwareEngineerService(SoftwareEngineerRepository softwareEngineerRepository) {
        this.softwareEngineerRepository = softwareEngineerRepository;
    }

    public List<SoftwareEngineerResponseDto> getSoftwareEngineers() {
        return softwareEngineerRepository.findAll()
                .stream()
                .map(se -> new SoftwareEngineerResponseDto(
                        se.getId(),
                        se.getName(),
                        se.getTechStack()
                ))
                .toList();
    }


    private SoftwareEngineer toEntity(SoftwareEngineerRequestDto dto) {
        SoftwareEngineer entity = new SoftwareEngineer();
        entity.setName(dto.getName());
        entity.setTechStack(dto.getTechStack());
        return entity;
    }

    private SoftwareEngineerResponseDto toResponseDto(SoftwareEngineer entity) {
        return new SoftwareEngineerResponseDto(
                entity.getId(),
                entity.getName(),
                entity.getTechStack()
        );
    }



    @Transactional
    public SoftwareEngineerResponseDto insertSoftwareEngineer(
            SoftwareEngineerRequestDto dto) {

        if (softwareEngineerRepository.existsByNameAndTechStack(
                dto.getName(), dto.getTechStack())) {

            throw new DuplicateResourceException(
                    "SoftwareEngineer already exists with name " +
                            dto.getName() + " and techStack " + dto.getTechStack()
            );
        }


        SoftwareEngineer entity = toEntity(dto);
        SoftwareEngineer saved = softwareEngineerRepository.save(entity);
        return toResponseDto(saved);
    }


    public SoftwareEngineerResponseDto getSoftwareEngineersById(Integer id) {

        SoftwareEngineer entity = softwareEngineerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "SoftwareEngineer not found with id " + id
                        )
                );

        return toResponseDto(entity);
    }



    @Transactional
    public void deleteSoftwareEngineerById(Integer id) {

        SoftwareEngineer entity = softwareEngineerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "SoftwareEngineer not found with id " + id
                        )
                );

        softwareEngineerRepository.delete(entity);
    }



    @Transactional
    public SoftwareEngineerResponseDto updateSoftwareEngineerById(
            Integer id,
            SoftwareEngineerRequestDto dto) {

        SoftwareEngineer entity = softwareEngineerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "SoftwareEngineer not found with id " + id
                        )
                );

        entity.setName(dto.getName());
        entity.setTechStack(dto.getTechStack());

        SoftwareEngineer updated = softwareEngineerRepository.save(entity);
        return toResponseDto(updated);
    }


}
