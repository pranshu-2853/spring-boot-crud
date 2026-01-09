package com.learning.dto;

import jakarta.validation.constraints.NotBlank;

public class SoftwareEngineerRequestDto {

    private String name;
    private String techStack;

    @NotBlank
    private String title;

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
