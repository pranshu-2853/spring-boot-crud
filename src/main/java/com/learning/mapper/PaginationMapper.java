package com.learning.mapper;

import com.learning.dto.PaginatedResponse;
import org.springframework.data.domain.Page;

public class PaginationMapper {

    private PaginationMapper() {
        // prevent instantiation
    }

    public static <T> PaginatedResponse<T> toPaginatedResponse(Page<T> page) {
        return new PaginatedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
