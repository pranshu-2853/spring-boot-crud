package com.learning.service;

import com.learning.dto.SoftwareEngineerFilter;
import com.learning.dto.SoftwareEngineerRequestDto;
import com.learning.dto.SoftwareEngineerResponseDto;
import com.learning.entity.SoftwareEngineer;
import com.learning.exception.DuplicateResourceException;
import com.learning.mapper.SoftwareEngineerMapper;
import com.learning.repository.SoftwareEngineerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import com.learning.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import static org.mockito.ArgumentMatchers.eq;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SoftwareEngineerServiceTest {

    @Mock
    private SoftwareEngineerRepository repository;

    @Mock
    private SoftwareEngineerMapper mapper;

    @InjectMocks
    private SoftwareEngineerService service;

    @Nested
    @DisplayName("Insert Software Engineer")
    class InsertSoftwareEngineerTests {

        @Test
        @DisplayName("Should throw exception when engineer with same name and tech stack already exists")
        void shouldThrowException_whenDuplicateExists() {

            SoftwareEngineerRequestDto request =
                    new SoftwareEngineerRequestDto("Pranshu", "Java");

            when(repository.existsByNameAndTechStack("Pranshu", "Java"))
                    .thenReturn(true);

            assertThrows(DuplicateResourceException.class, () ->
                    service.insertSoftwareEngineer(request)
            );

            verify(repository, never()).save(any());
            verify(mapper, never()).toEntity(any());
        }

        @Test
        @DisplayName("Should save engineer when no duplicate exists")
        void shouldSave_whenNotDuplicate() {

            SoftwareEngineerRequestDto request =
                    new SoftwareEngineerRequestDto("Pranshu", "Java");

            SoftwareEngineer entity =
                    new SoftwareEngineer(null, "Pranshu", "Java");

            SoftwareEngineer savedEntity =
                    new SoftwareEngineer(1, "Pranshu", "Java");

            SoftwareEngineerResponseDto responseDto =
                    new SoftwareEngineerResponseDto(1, "Pranshu", "Java");

            when(repository.existsByNameAndTechStack("Pranshu", "Java"))
                    .thenReturn(false);

            when(mapper.toEntity(request))
                    .thenReturn(entity);

            when(repository.save(entity))
                    .thenReturn(savedEntity);

            when(mapper.toResponseDto(savedEntity))
                    .thenReturn(responseDto);

            SoftwareEngineerResponseDto result =
                    service.insertSoftwareEngineer(request);

            assertNotNull(result);
            assertEquals("Pranshu", result.getName());
            assertEquals("Java", result.getTechStack());

            verify(repository, times(1))
                    .existsByNameAndTechStack("Pranshu", "Java");

            verify(mapper, times(1)).toEntity(request);
            verify(repository, times(1)).save(entity);
            verify(mapper, times(1)).toResponseDto(savedEntity);
        }
    }
    @Nested
    @DisplayName("Get Software Engineer By Id")
    class GetSoftwareEngineerByIdTests {

        @Test
        @DisplayName("Should return engineer when id exists")
        void shouldReturnEngineer_whenIdExists() {

            Integer id = 1;

            SoftwareEngineer entity =
                    new SoftwareEngineer(1, "Pranshu", "Java");

            SoftwareEngineerResponseDto responseDto =
                    new SoftwareEngineerResponseDto(1, "Pranshu", "Java");

            when(repository.findById(id))
                    .thenReturn(Optional.of(entity));

            when(mapper.toResponseDto(entity))
                    .thenReturn(responseDto);

            SoftwareEngineerResponseDto result =
                    service.getSoftwareEngineersById(id);

            assertNotNull(result);
            assertEquals(1, result.getId());
            assertEquals("Pranshu", result.getName());
            assertEquals("Java", result.getTechStack());

            verify(repository, times(1)).findById(id);
            verify(mapper, times(1)).toResponseDto(entity);
        }

        @Test
        @DisplayName("Should throw exception when id does not exist")
        void shouldThrowException_whenIdDoesNotExist() {

            Integer id = 99;

            when(repository.findById(id))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () ->
                    service.getSoftwareEngineersById(id)
            );

            verify(repository, times(1)).findById(id);
            verify(mapper, never()).toResponseDto(any());
        }
    }

    @Nested
    @DisplayName("Update Software Engineer By Id")
    class UpdateTests {
        @Test
        @DisplayName("Should update engineer when id exists")
        void shouldUpdateEngineer_whenIdExists() {

            Integer id = 1;

            // Existing entity in database
            SoftwareEngineer existing =
                    new SoftwareEngineer(1, "OldName", "OldTech");

            // Incoming update request
            SoftwareEngineerRequestDto request =
                    new SoftwareEngineerRequestDto("NewName", "NewTech");

            // Mock repository findById
            when(repository.findById(id))
                    .thenReturn(Optional.of(existing));

            // We don’t care what save returns here, just return same entity
            when(repository.save(any(SoftwareEngineer.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // Mock mapper
            SoftwareEngineerResponseDto responseDto =
                    new SoftwareEngineerResponseDto(1, "NewName", "NewTech");

            when(mapper.toResponseDto(any(SoftwareEngineer.class)))
                    .thenReturn(responseDto);

            // ACT
            SoftwareEngineerResponseDto result =
                    service.updateSoftwareEngineerById(id, request);

            // ASSERT RESULT
            assertNotNull(result);
            assertEquals("NewName", result.getName());
            assertEquals("NewTech", result.getTechStack());

            // CAPTURE SAVED ENTITY
            ArgumentCaptor<SoftwareEngineer> captor =
                    ArgumentCaptor.forClass(SoftwareEngineer.class);

            verify(repository).save(captor.capture());

            SoftwareEngineer captured = captor.getValue();

            // VERIFY MUTATION
            assertEquals("NewName", captured.getName());
            assertEquals("NewTech", captured.getTechStack());

            // VERIFY INTERACTIONS
            verify(repository).findById(id);
            verify(mapper).toResponseDto(captured);
        }
        @Test
        @DisplayName("Should throw exception when id does not exist")
        void shouldThrowException_whenIdDoesNotExist() {

            Integer id = 99;

            when(repository.findById(id))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () ->
                    service.updateSoftwareEngineerById(id,
                            new SoftwareEngineerRequestDto("NewName", "NewTech"))
            );

            verify(repository).findById(id);
            verify(repository, never()).save(any());
            verify(mapper, never()).toResponseDto(any());
        }
    }

    @Nested
    @DisplayName("Delete Software Engineer By Id")
    class DeleteTests {
        @Test
        @DisplayName("Should delete engineer when id exists")
        void shouldDeleteEngineer_whenIdExists() {

            Integer id = 1;

            SoftwareEngineer entity =
                    new SoftwareEngineer(1, "Pranshu", "Java");

            when(repository.findById(id))
                    .thenReturn(Optional.of(entity));

            service.deleteSoftwareEngineerById(id);

            verify(repository).findById(id);
            verify(repository).delete(entity);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existing id")
        void shouldThrowException_whenIdDoesNotExist() {

            Integer id = 99;

            when(repository.findById(id))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () ->
                    service.deleteSoftwareEngineerById(id)
            );

            verify(repository).findById(id);
            verify(repository, never())
                    .delete(any(SoftwareEngineer.class));
        }
    }

    @Nested
    @DisplayName("Get Software Engineers With Pagination")
    class PaginationTests {
        @Test
        @DisplayName("Should return paginated engineers")
        void shouldReturnPaginatedEngineers() {

            Pageable pageable = PageRequest.of(0, 2);

            SoftwareEngineer entity1 =
                    new SoftwareEngineer(1, "A", "Java");
            SoftwareEngineer entity2 =
                    new SoftwareEngineer(2, "B", "Spring");

            List<SoftwareEngineer> entityList =
                    List.of(entity1, entity2);

            Page<SoftwareEngineer> page =
                    new PageImpl<>(entityList, pageable, 2);

            when(repository.findAll(pageable))
                    .thenReturn(page);

            when(mapper.toResponseDto(entity1))
                    .thenReturn(new SoftwareEngineerResponseDto(1, "A", "Java"));

            when(mapper.toResponseDto(entity2))
                    .thenReturn(new SoftwareEngineerResponseDto(2, "B", "Spring"));

            Page<SoftwareEngineerResponseDto> result =
                    service.getSoftwareEngineers(pageable);

            assertEquals(2, result.getContent().size());

            verify(repository).findAll(pageable);
            verify(mapper, times(2)).toResponseDto(any());
        }
    }

    @Nested
    @DisplayName("Get Software Engineers With Filter And Pagination")
    class FilterTests {
        @Test
        @DisplayName("Should return filtered paginated engineers")
        void shouldReturnFilteredEngineers() {

            SoftwareEngineerFilter filter = new SoftwareEngineerFilter();
            Pageable pageable = PageRequest.of(0, 2);

            SoftwareEngineer entity =
                    new SoftwareEngineer(1, "Pranshu", "Java");

            Page<SoftwareEngineer> page =
                    new PageImpl<>(List.of(entity), pageable, 1);

            when(repository.findAll(any(Specification.class), eq(pageable)))
                    .thenReturn(page);

            when(mapper.toResponseDto(entity))
                    .thenReturn(new SoftwareEngineerResponseDto(1, "Pranshu", "Java"));

            Page<SoftwareEngineerResponseDto> result =
                    service.getSoftwareEngineers(filter, pageable);

            assertEquals(1, result.getContent().size());

            verify(repository)
                    .findAll(any(Specification.class), eq(pageable));
            verify(mapper).toResponseDto(entity);
        }
    }
}