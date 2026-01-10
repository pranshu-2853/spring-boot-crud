package com.learning.dto;

import jakarta.validation.constraints.NotBlank;

public class SoftwareEngineerRequestDto {

    @NotBlank
    private String name;

    @NotBlank
    private String techStack;

    public SoftwareEngineerRequestDto() {
    }

    public SoftwareEngineerRequestDto(String name, String techStack) {
        this.name = name;
        this.techStack = techStack;
    }

    public String getName() {
        return name;
    }

    public String getTechStack() {
        return techStack;
    }
}
