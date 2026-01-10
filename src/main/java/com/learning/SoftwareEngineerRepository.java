package com.learning;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SoftwareEngineerRepository extends JpaRepository<SoftwareEngineer,Integer> {

    boolean existsByNameAndTechStack(String name, String techStack);

}
