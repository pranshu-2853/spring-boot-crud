package com.learning;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SoftwareEngineerRepository
        extends JpaRepository<SoftwareEngineer, Integer>,
        JpaSpecificationExecutor<SoftwareEngineer> {

    boolean existsByNameAndTechStack(String name, String techStack);
}
