package com.learning.controller;

import com.learning.dto.*;
import com.learning.exception.DuplicateResourceException;
import com.learning.exception.GlobalExceptionHandler;
import com.learning.exception.ResourceNotFoundException;
import com.learning.security.JwtAuthenticationFilter;
import com.learning.security.JwtService;
import com.learning.service.SoftwareEngineerService;
import com.learning.mapper.PaginationMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SoftwareEngineerController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class SoftwareEngineerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SoftwareEngineerService softwareEngineerService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtService jwtService;

    private final String BASE_URL = "/api/v1/software-engineer";

    // =====================================
    // GET PAGINATED WITH FILTER
    // =====================================

    @Test
    @DisplayName("Should return paginated engineers")
    void shouldReturnPaginatedEngineers() throws Exception {

        List<SoftwareEngineerResponseDto> content = List.of(
                new SoftwareEngineerResponseDto(1, "Pranshu", "Java")
        );

        Page<SoftwareEngineerResponseDto> page =
                new PageImpl<>(content, PageRequest.of(0,5),1);

        when(softwareEngineerService.getSoftwareEngineers(any(), any()))
                .thenReturn(page);

        PaginatedResponse<SoftwareEngineerResponseDto> paginatedResponse =
                new PaginatedResponse<>(content, 0,5,1,1,true);

        try (MockedStatic<PaginationMapper> mocked =
                     mockStatic(PaginationMapper.class)) {

            mocked.when(() -> PaginationMapper.toPaginatedResponse(page))
                    .thenReturn(paginatedResponse);

            mockMvc.perform(get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data[0].name").value("Pranshu"));
        }
    }

    // =====================================
    // GET BY ID
    // =====================================

    @Nested
    class GetByIdTests {

        @Test
        void shouldReturnEngineerById() throws Exception {

            SoftwareEngineerResponseDto response =
                    new SoftwareEngineerResponseDto(1, "Pranshu", "Java");

            when(softwareEngineerService.getSoftwareEngineersById(1))
                    .thenReturn(response);

            mockMvc.perform(get(BASE_URL + "/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Pranshu"));
        }

        @Test
        void shouldReturn404IfNotFound() throws Exception {

            when(softwareEngineerService.getSoftwareEngineersById(99))
                    .thenThrow(new ResourceNotFoundException(
                            "SoftwareEngineer not found with id 99"
                    ));

            mockMvc.perform(get(BASE_URL + "/99"))
                    .andExpect(status().isNotFound());
        }
    }

    // =====================================
    // POST
    // =====================================

    @Nested
    class AddTests {

        @Test
        void shouldCreateEngineer() throws Exception {

            SoftwareEngineerResponseDto response =
                    new SoftwareEngineerResponseDto(1, "Pranshu", "Java");

            when(softwareEngineerService.insertSoftwareEngineer(any()))
                    .thenReturn(response);

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                {
                                  "name": "Pranshu",
                                  "techStack": "Java"
                                }
                                """))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        void shouldReturn409WhenDuplicate() throws Exception {

            when(softwareEngineerService.insertSoftwareEngineer(any()))
                    .thenThrow(new DuplicateResourceException(
                            "SoftwareEngineer already exists"
                    ));

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                {
                                  "name": "Pranshu",
                                  "techStack": "Java"
                                }
                                """))
                    .andExpect(status().isConflict());
        }

        @Test
        void shouldReturn400WhenValidationFails() throws Exception {

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                {
                                  "name": "",
                                  "techStack": ""
                                }
                                """))
                    .andExpect(status().isBadRequest());
        }
    }

    // =====================================
    // PUT
    // =====================================

    @Nested
    class UpdateTests {

        @Test
        void shouldUpdateEngineer() throws Exception {

            SoftwareEngineerResponseDto response =
                    new SoftwareEngineerResponseDto(1, "Pranshu", "Spring");

            when(softwareEngineerService.updateSoftwareEngineerById(any(), any()))
                    .thenReturn(response);

            mockMvc.perform(put(BASE_URL + "/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                {
                                  "name": "Pranshu",
                                  "techStack": "Spring"
                                }
                                """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.techStack").value("Spring"));
        }

        @Test
        void shouldReturn404WhenUpdatingMissing() throws Exception {

            when(softwareEngineerService.updateSoftwareEngineerById(any(), any()))
                    .thenThrow(new ResourceNotFoundException(
                            "SoftwareEngineer not found with id 99"
                    ));

            mockMvc.perform(put(BASE_URL + "/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                {
                                  "name": "Pranshu",
                                  "techStack": "Spring"
                                }
                                """))
                    .andExpect(status().isNotFound());
        }
    }

    // =====================================
    // DELETE
    // =====================================

    @Nested
    class DeleteTests {

        @Test
        void shouldDeleteEngineer() throws Exception {

            doNothing().when(softwareEngineerService)
                    .deleteSoftwareEngineerById(1);

            mockMvc.perform(delete(BASE_URL + "/1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        void shouldReturn404WhenDeletingMissing() throws Exception {

            doThrow(new ResourceNotFoundException(
                    "SoftwareEngineer not found with id 99"
            )).when(softwareEngineerService)
                    .deleteSoftwareEngineerById(99);

            mockMvc.perform(delete(BASE_URL + "/99"))
                    .andExpect(status().isNotFound());
        }
    }


    @Nested
    @DisplayName("GET /software-engineer - Filter Parameter Tests")
    class FilterTests {

        @Test
        @DisplayName("Should pass name and techStack parameters correctly")
        void shouldPassFilterParametersCorrectly() throws Exception {

            Page<SoftwareEngineerResponseDto> page =
                    Page.empty(PageRequest.of(0, 5));

            when(softwareEngineerService.getSoftwareEngineers(any(), any()))
                    .thenReturn(page);

            try (MockedStatic<PaginationMapper> mocked =
                         mockStatic(PaginationMapper.class)) {

                mocked.when(() -> PaginationMapper.toPaginatedResponse(page))
                        .thenReturn(new PaginatedResponse<>(List.of(), 0, 5, 0, 0, true));

                mockMvc.perform(get(BASE_URL)
                                .param("name", "Pranshu")
                                .param("techStack", "Java"))
                        .andExpect(status().isOk());
            }

            verify(softwareEngineerService)
                    .getSoftwareEngineers(
                            argThat(filter ->
                                    "Pranshu".equals(filter.getName()) &&
                                            "Java".equals(filter.getTechStack())
                            ),
                            any(Pageable.class)
                    );
        }

        @Test
        @DisplayName("Should handle missing filter parameters")
        void shouldHandleMissingFilterParameters() throws Exception {

            Page<SoftwareEngineerResponseDto> page =
                    Page.empty(PageRequest.of(0, 5));

            when(softwareEngineerService.getSoftwareEngineers(any(), any()))
                    .thenReturn(page);

            try (MockedStatic<PaginationMapper> mocked =
                         mockStatic(PaginationMapper.class)) {

                mocked.when(() -> PaginationMapper.toPaginatedResponse(page))
                        .thenReturn(new PaginatedResponse<>(List.of(), 0, 5, 0, 0, true));

                mockMvc.perform(get(BASE_URL))
                        .andExpect(status().isOk());
            }

            verify(softwareEngineerService)
                    .getSoftwareEngineers(any(), any(Pageable.class));
        }
    }


    @Nested
    @DisplayName("GET /software-engineer - Pageable Tests")
    class PageableTests {

        @Test
        @DisplayName("Should use custom page and size parameters")
        void shouldPassCustomPageableParameters() throws Exception {

            Page<SoftwareEngineerResponseDto> page =
                    Page.empty(PageRequest.of(2, 10));

            when(softwareEngineerService.getSoftwareEngineers(any(), any()))
                    .thenReturn(page);

            try (MockedStatic<PaginationMapper> mocked =
                         mockStatic(PaginationMapper.class)) {

                mocked.when(() -> PaginationMapper.toPaginatedResponse(page))
                        .thenReturn(new PaginatedResponse<>(List.of(), 0, 5, 0, 0, true));

                mockMvc.perform(get(BASE_URL)
                                .param("page", "2")
                                .param("size", "10"))
                        .andExpect(status().isOk());
            }

            verify(softwareEngineerService)
                    .getSoftwareEngineers(
                            any(),
                            argThat(pageable ->
                                    pageable.getPageNumber() == 2 &&
                                            pageable.getPageSize() == 10
                            )
                    );
        }

        @Test
        @DisplayName("Should use default pageable when no params provided")
        void shouldUseDefaultPageable() throws Exception {

            Page<SoftwareEngineerResponseDto> page =
                    Page.empty(PageRequest.of(0, 5));

            when(softwareEngineerService.getSoftwareEngineers(any(), any()))
                    .thenReturn(page);

            try (MockedStatic<PaginationMapper> mocked =
                         mockStatic(PaginationMapper.class)) {

                mocked.when(() -> PaginationMapper.toPaginatedResponse(page))
                        .thenReturn(new PaginatedResponse<>(List.of(), 0, 5, 0, 0, true));

                mockMvc.perform(get(BASE_URL))
                        .andExpect(status().isOk());
            }

            verify(softwareEngineerService)
                    .getSoftwareEngineers(
                            any(),
                            argThat(pageable ->
                                    pageable.getPageNumber() == 0 &&
                                            pageable.getPageSize() == 5
                            )
                    );
        }
    }

    @Test
    @DisplayName("Should return structured validation errors")
    void shouldReturnValidationErrors() throws Exception {

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "name": "",
                          "techStack": ""
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.techStack").exists());
    }
}