package com.learning.dto;

public class SoftwareEngineerRequestDto {

    private String name;
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
