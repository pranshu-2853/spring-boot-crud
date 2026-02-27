package com.learning.service;

import com.learning.repository.SoftwareEngineerRepository;
import com.learning.entity.SoftwareEngineer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.learning.dto.SoftwareEngineerFilter;
import com.learning.repository.specification.SoftwareEngineerSpecification;
import org.springframework.data.jpa.domain.Specification;
import com.learning.mapper.SoftwareEngineerMapper;
import com.learning.exception.DuplicateResourceException;
import com.learning.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import com.learning.dto.SoftwareEngineerRequestDto;
import com.learning.dto.SoftwareEngineerResponseDto;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class SoftwareEngineerService {

    private final SoftwareEngineerRepository softwareEngineerRepository;
    private final SoftwareEngineerMapper mapper;

    private static final Logger logger =
            LoggerFactory.getLogger(SoftwareEngineerService.class);

    public SoftwareEngineerService(
            SoftwareEngineerRepository softwareEngineerRepository,
            SoftwareEngineerMapper mapper) {

        this.softwareEngineerRepository = softwareEngineerRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public Page<SoftwareEngineerResponseDto> getSoftwareEngineers(
            SoftwareEngineerFilter filter,
            Pageable pageable) {

        logger.info("Fetching engineers with filter name={} techStack={} page={} size={}",
                filter.getName(),
                filter.getTechStack(),
                pageable.getPageNumber(),
                pageable.getPageSize());

        Specification<SoftwareEngineer> specification =
                SoftwareEngineerSpecification.withFilter(filter);

        return softwareEngineerRepository
                .findAll(specification, pageable)
                .map(mapper::toResponseDto);
    }

    @Transactional(readOnly = true)
    public List<SoftwareEngineerResponseDto> getSoftwareEngineers() {

        logger.info("Fetching all engineers without pagination");

        return softwareEngineerRepository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<SoftwareEngineerResponseDto> getSoftwareEngineers(Pageable pageable) {

        logger.info("Fetching engineers page={} size={}",
                pageable.getPageNumber(),
                pageable.getPageSize());

        return softwareEngineerRepository
                .findAll(pageable)
                .map(mapper::toResponseDto);
    }

    @Transactional
    public SoftwareEngineerResponseDto insertSoftwareEngineer(
            SoftwareEngineerRequestDto dto) {

        logger.info("Attempting to create engineer name={} techStack={}",
                dto.getName(),
                dto.getTechStack());

        if (softwareEngineerRepository.existsByNameAndTechStack(
                dto.getName(), dto.getTechStack())) {

            logger.warn("Duplicate engineer detected name={} techStack={}",
                    dto.getName(),
                    dto.getTechStack());

            throw new DuplicateResourceException(
                    "SoftwareEngineer already exists with name " +
                            dto.getName() + " and techStack " + dto.getTechStack()
            );
        }

        SoftwareEngineer entity = mapper.toEntity(dto);
        SoftwareEngineer saved = softwareEngineerRepository.save(entity);

        logger.info("Engineer created successfully id={}", saved.getId());

        return mapper.toResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public SoftwareEngineerResponseDto getSoftwareEngineersById(Integer id) {

        logger.info("Fetching engineer id={}", id);

        SoftwareEngineer entity = softwareEngineerRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Engineer not found id={}", id);
                    return new ResourceNotFoundException(
                            "SoftwareEngineer not found with id " + id
                    );
                });

        return mapper.toResponseDto(entity);
    }

    @Transactional
    public void deleteSoftwareEngineerById(Integer id) {

        logger.info("Attempting to delete engineer id={}", id);

        SoftwareEngineer entity = softwareEngineerRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Delete failed - engineer not found id={}", id);
                    return new ResourceNotFoundException(
                            "SoftwareEngineer not found with id " + id
                    );
                });

        softwareEngineerRepository.delete(entity);

        logger.info("Engineer deleted successfully id={}", id);
    }

    @Transactional
    public SoftwareEngineerResponseDto updateSoftwareEngineerById(
            Integer id,
            SoftwareEngineerRequestDto dto) {

        logger.info("Attempting to update engineer id={}", id);

        SoftwareEngineer entity = softwareEngineerRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Update failed - engineer not found id={}", id);
                    return new ResourceNotFoundException(
                            "SoftwareEngineer not found with id " + id
                    );
                });

        entity.setName(dto.getName());
        entity.setTechStack(dto.getTechStack());

        SoftwareEngineer updated = softwareEngineerRepository.save(entity);

        logger.info("Engineer updated successfully id={}", id);

        return mapper.toResponseDto(updated);
    }
}