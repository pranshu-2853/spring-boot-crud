package com.learning.repository.specification;

import com.learning.SoftwareEngineer;
import com.learning.dto.SoftwareEngineerFilter;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class SoftwareEngineerSpecification {

    private SoftwareEngineerSpecification() {
        // prevent instantiation
    }

    public static Specification<SoftwareEngineer> withFilter(
            SoftwareEngineerFilter filter) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // filter by name (case-insensitive, partial match)
            if (filter.getName() != null && !filter.getName().isBlank()) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("name")),
                                "%" + filter.getName().toLowerCase() + "%"
                        )
                );
            }

            // filter by techStack (case-insensitive, exact match)
            if (filter.getTechStack() != null && !filter.getTechStack().isBlank()) {
                predicates.add(
                        cb.equal(
                                cb.lower(root.get("techStack")),
                                filter.getTechStack().toLowerCase()
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
