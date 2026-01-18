package com.learning;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.learning.mapper.SoftwareEngineerMapper;
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
    private final SoftwareEngineerMapper mapper;

    public SoftwareEngineerService(
            SoftwareEngineerRepository softwareEngineerRepository,
            SoftwareEngineerMapper mapper) {

        this.softwareEngineerRepository = softwareEngineerRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<SoftwareEngineerResponseDto> getSoftwareEngineers() {
        return softwareEngineerRepository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<SoftwareEngineerResponseDto> getSoftwareEngineers(Pageable pageable) {

        return softwareEngineerRepository
                .findAll(pageable)
                .map(mapper::toResponseDto);
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

        SoftwareEngineer entity = mapper.toEntity(dto);
        SoftwareEngineer saved = softwareEngineerRepository.save(entity);
        return mapper.toResponseDto(saved);
    }



    @Transactional(readOnly = true)
    public SoftwareEngineerResponseDto getSoftwareEngineersById(Integer id) {

        SoftwareEngineer entity = softwareEngineerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "SoftwareEngineer not found with id " + id
                        )
                );

        return mapper.toResponseDto(entity);
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
        return mapper.toResponseDto(updated);
    }



}
