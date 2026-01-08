package com.learning;

import org.springframework.stereotype.Service;
import com.learning.dto.SoftwareEngineerRequestDto;
import com.learning.dto.SoftwareEngineerResponseDto;


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


    public SoftwareEngineerResponseDto insertSoftwareEngineer(
            SoftwareEngineerRequestDto dto) {

        SoftwareEngineer entity = new SoftwareEngineer();
        entity.setName(dto.getName());
        entity.setTechStack(dto.getTechStack());

        SoftwareEngineer saved = softwareEngineerRepository.save(entity);

        return new SoftwareEngineerResponseDto(
                saved.getId(),
                saved.getName(),
                saved.getTechStack()
        );
    }


    public SoftwareEngineerResponseDto getSoftwareEngineersById(Integer id) {

        SoftwareEngineer se = softwareEngineerRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException(id + " not found"));

        return new SoftwareEngineerResponseDto(
                se.getId(),
                se.getName(),
                se.getTechStack()
        );
    }


    public void deleteSoftwareEngineerById(Integer id) {

        boolean exists = softwareEngineerRepository.existsById(id);
        if (!exists) {
            throw new IllegalStateException(id + " not found");
        }

        softwareEngineerRepository.deleteById(id);
    }


    public SoftwareEngineerResponseDto updateSoftwareEngineerById(
            Integer id,
            SoftwareEngineerRequestDto dto) {

        SoftwareEngineer existing = softwareEngineerRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException(id + " not found"));

        existing.setName(dto.getName());
        existing.setTechStack(dto.getTechStack());

        SoftwareEngineer saved = softwareEngineerRepository.save(existing);

        return new SoftwareEngineerResponseDto(
                saved.getId(),
                saved.getName(),
                saved.getTechStack()
        );
    }

}
