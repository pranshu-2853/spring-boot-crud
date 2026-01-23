package com.learning.dto;

public class SoftwareEngineerFilter {

    // partial, case-insensitive search
    private String name;

    // exact or partial tech stack match
    private String techStack;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTechStack() {
        return techStack;
    }

    public void setTechStack(String techStack) {
        this.techStack = techStack;
    }
}
