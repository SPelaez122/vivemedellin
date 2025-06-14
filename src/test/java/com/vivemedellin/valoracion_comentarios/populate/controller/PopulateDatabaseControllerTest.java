package com.vivemedellin.valoracion_comentarios.populate.controller;


import com.vivemedellin.valoracion_comentarios.category.dto.CategoryDTO;
import com.vivemedellin.valoracion_comentarios.event.dto.EventDto;
import com.vivemedellin.valoracion_comentarios.organizer.dto.OrganizerDTO;
import com.vivemedellin.valoracion_comentarios.populate.service.PopulateDatabaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PopulateDatabaseControllerTest {

    private PopulateDatabaseService populateDatabaseService;
    private PopulateDatabaseController populateDatabaseController;

    @BeforeEach
    void setUp() {
        populateDatabaseService = mock(PopulateDatabaseService.class);
        populateDatabaseController = new PopulateDatabaseController(populateDatabaseService);
    }

    @Test
    void testPopulateCategories() {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(1L);
        categoryDTO.setName("Music");

        when(populateDatabaseService.populateCategories()).thenReturn(List.of(categoryDTO));

        ResponseEntity<List<CategoryDTO>> response = populateDatabaseController.populateCategories();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Music", response.getBody().get(0).getName());

        verify(populateDatabaseService).populateCategories();
    }

    @Test
    void testPopulateOrganizers() {
        OrganizerDTO organizerDTO = new OrganizerDTO();
        organizerDTO.setId(1L);
        organizerDTO.setFirstName("Organizer Test");
        organizerDTO.setEmail("organizer@example.com");

        when(populateDatabaseService.populateOrganizers()).thenReturn(List.of(organizerDTO));

        ResponseEntity<List<OrganizerDTO>> response = populateDatabaseController.populateOrganizers();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Organizer Test", response.getBody().get(0).getFirstName());

        verify(populateDatabaseService).populateOrganizers();
    }

    @Test
    void testPopulateEvents() {
        EventDto eventDto = new EventDto();
        eventDto.setId(1L);
        eventDto.setTitle("Sample Event");
        eventDto.setDescription("Description of event");

        when(populateDatabaseService.populateEvents()).thenReturn(eventDto);

        ResponseEntity<EventDto> response = populateDatabaseController.populateEvents();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Sample Event", response.getBody().getTitle());

        verify(populateDatabaseService).populateEvents();
    }
}
