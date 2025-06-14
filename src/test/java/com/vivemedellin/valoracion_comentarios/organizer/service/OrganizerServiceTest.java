package com.vivemedellin.valoracion_comentarios.organizer.service;

import com.vivemedellin.valoracion_comentarios.organizer.dto.OrganizerDTO;
import com.vivemedellin.valoracion_comentarios.organizer.entity.Organizer;
import com.vivemedellin.valoracion_comentarios.organizer.factory.OrganizerMockFactory;
import com.vivemedellin.valoracion_comentarios.organizer.mapper.OrganizerMapper;
import com.vivemedellin.valoracion_comentarios.organizer.repository.OrganizerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrganizerServiceTest {

    private OrganizerRepository organizerRepository;
    private OrganizerMapper organizerMapper;
    private OrganizerMockFactory organizerMockFactory;
    private OrganizerService organizerService;

    @BeforeEach
    void setUp() {
        organizerRepository = mock(OrganizerRepository.class);
        organizerMapper = mock(OrganizerMapper.class);
        organizerMockFactory = mock(OrganizerMockFactory.class);

        organizerService = new OrganizerService(organizerRepository, organizerMapper, organizerMockFactory);
    }

    @Test
    void testPopulateDatabaseSuccessfully() {
        Organizer organizerEntity = new Organizer();

        OrganizerDTO organizerDto = new OrganizerDTO();
        organizerDto.setId(UUID.randomUUID().getMostSignificantBits());
        organizerDto.setFirstName("Organizer Test");
        organizerDto.setEmail("organizer@example.com");

        when(organizerMockFactory.createMock()).thenReturn(organizerEntity);
        when(organizerRepository.save(organizerEntity)).thenReturn(organizerEntity);
        when(organizerMapper.toDTO(organizerEntity)).thenReturn(organizerDto);

        List<OrganizerDTO> result = organizerService.populateDatabase();

        assertNotNull(result);
        assertEquals(10, result.size());
        for (OrganizerDTO dto : result) {
            assertNotNull(dto.getId());
            assertNotNull(dto.getFirstName());
            assertNotNull(dto.getEmail());
        }

        verify(organizerMockFactory, times(10)).createMock();
        verify(organizerRepository, times(10)).save(organizerEntity);
        verify(organizerMapper, times(10)).toDTO(organizerEntity);
    }
}
