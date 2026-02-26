package com.learning.controller;

import com.learning.service.SoftwareEngineerService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springdoc.core.annotations.ParameterObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


@RestController
@RequestMapping("api/v1/software-engineer")
public class SoftwareEngineerController {

    private final SoftwareEngineerService softwareEngineerService;

    public SoftwareEngineerController(SoftwareEngineerService softwareEngineerService) {
        this.softwareEngineerService = softwareEngineerService;
    }



    @Operation(
            summary = "Get all software engineers with optional filtering and pagination",
            description = "Accessible by USER and ADMIN roles. Supports name and techStack filtering."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient role")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<PaginatedResponse<SoftwareEngineerResponseDto>> getSoftwareEngineers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String techStack,
            @ParameterObject
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



    @Operation(summary = "Get software engineer by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Engineer retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Engineer not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public SoftwareEngineerResponseDto getSoftwareEngineerById(
            @PathVariable Integer id) {

        return softwareEngineerService.getSoftwareEngineersById(id);
    }



    @Operation(summary = "Create a new software engineer (ADMIN only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Engineer created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SoftwareEngineerResponseDto> addSoftwareEngineer(
            @Valid @RequestBody SoftwareEngineerRequestDto dto) {

        SoftwareEngineerResponseDto response =
                softwareEngineerService.insertSoftwareEngineer(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }




    @Operation(summary = "Delete a software engineer by ID (ADMIN only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Engineer deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Engineer not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSoftwareEngineerById(@PathVariable Integer id) {
        softwareEngineerService.deleteSoftwareEngineerById(id);
    }




    @Operation(summary = "Update an existing software engineer by ID (ADMIN only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Engineer updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Engineer not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public SoftwareEngineerResponseDto updateSoftwareEngineerById(
            @PathVariable Integer id,
            @Valid @RequestBody SoftwareEngineerRequestDto dto) {

        return softwareEngineerService.updateSoftwareEngineerById(id, dto);
    }
}
