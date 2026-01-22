package com.learning;

import com.learning.dto.PaginatedResponse;
import com.learning.mapper.PaginationMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.learning.dto.SoftwareEngineerFilter;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.learning.dto.SoftwareEngineerRequestDto;
import com.learning.dto.SoftwareEngineerResponseDto;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("api/v1/software-engineer")
public class SoftwareEngineerController {

    private final SoftwareEngineerService softwareEngineerService;

    public SoftwareEngineerController(SoftwareEngineerService softwareEngineerService) {
        this.softwareEngineerService = softwareEngineerService;
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<SoftwareEngineerResponseDto>> getSoftwareEngineers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String techStack,
            @PageableDefault(
                    page = 0,
                    size = 5,
                    sort = "id",
                    direction = Sort.Direction.ASC
            )
            Pageable pageable) {

        SoftwareEngineerFilter filter = new SoftwareEngineerFilter();
        filter.setName(name);
        filter.setTechStack(techStack);

        Page<SoftwareEngineerResponseDto> page =
                softwareEngineerService.getSoftwareEngineers(filter, pageable);

        PaginatedResponse<SoftwareEngineerResponseDto> response =
                PaginationMapper.toPaginatedResponse(page);

        return ResponseEntity.ok(response);
    }





    @GetMapping("/{id}")
    public SoftwareEngineerResponseDto getSoftwareEngineerById(
            @PathVariable Integer id) {

        return softwareEngineerService.getSoftwareEngineersById(id);
    }


    @PostMapping
    public ResponseEntity<SoftwareEngineerResponseDto> addSoftwareEngineer(
            @Valid @RequestBody SoftwareEngineerRequestDto dto) {

        SoftwareEngineerResponseDto response =
                softwareEngineerService.insertSoftwareEngineer(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }




    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSoftwareEngineerById(@PathVariable Integer id) {
        softwareEngineerService.deleteSoftwareEngineerById(id);
    }


    @PutMapping("/{id}")
    public SoftwareEngineerResponseDto updateSoftwareEngineerById(
            @PathVariable Integer id,
            @Valid @RequestBody SoftwareEngineerRequestDto dto) {

        return softwareEngineerService.updateSoftwareEngineerById(id, dto);
    }

}
