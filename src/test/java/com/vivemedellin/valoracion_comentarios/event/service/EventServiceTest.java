package com.vivemedellin.valoracion_comentarios.event.service;

import com.vivemedellin.valoracion_comentarios.admin.entity.Admin;
import com.vivemedellin.valoracion_comentarios.admin.repository.AdminRepository;
import com.vivemedellin.valoracion_comentarios.category.entity.Category;
import com.vivemedellin.valoracion_comentarios.category.service.CategoryRepository;
import com.vivemedellin.valoracion_comentarios.event.dto.EventDto;
import com.vivemedellin.valoracion_comentarios.event.dto.EventWithReviewStatsDTO;
import com.vivemedellin.valoracion_comentarios.event.entity.Event;
import com.vivemedellin.valoracion_comentarios.event.factory.EventMockFactory;
import com.vivemedellin.valoracion_comentarios.event.mapper.EventMapper;
import com.vivemedellin.valoracion_comentarios.event.repository.EventRepository;
import com.vivemedellin.valoracion_comentarios.organizer.entity.Organizer;
import com.vivemedellin.valoracion_comentarios.organizer.repository.OrganizerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventServiceTest {

    private EventRepository eventRepository;
    private CategoryRepository categoryRepository;
    private OrganizerRepository organizerRepository;
    private AdminRepository adminRepository;
    private EventMockFactory eventMockFactory;
    private EventMapper eventMapper;

    private EventService eventService;

    @BeforeEach
    void setUp() {
        eventRepository = mock(EventRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        organizerRepository = mock(OrganizerRepository.class);
        adminRepository = mock(AdminRepository.class);
        eventMockFactory = mock(EventMockFactory.class);
        eventMapper = mock(EventMapper.class);

        eventService = new EventService(eventRepository, categoryRepository, organizerRepository, adminRepository, eventMockFactory, eventMapper);
    }

    @Test
    void testGetAll() {
        List<EventWithReviewStatsDTO> mockList = List.of(mock(EventWithReviewStatsDTO.class));
        when(eventRepository.findAllWithReviewStats()).thenReturn(mockList);

        var result = eventService.getAll();

        assertEquals(1, result.size());
        verify(eventRepository).findAllWithReviewStats();
    }

    @Test
    void testGetById() {
        EventWithReviewStatsDTO dto = mock(EventWithReviewStatsDTO.class);
        when(eventRepository.findWithReviewStats(1L)).thenReturn(dto);

        var result = eventService.getById(1L);

        assertEquals(dto, result);
        verify(eventRepository).findWithReviewStats(1L);
    }

    @Test
    void testPopulateDatabaseSuccess() {
        Admin admin = new Admin();
        Category category = new Category();
        Organizer organizer = new Organizer();
        Event event = new Event();
        EventDto expectedDto = new EventDto();

        when(adminRepository.findAll()).thenReturn(List.of(admin));
        when(categoryRepository.findAll()).thenReturn(List.of(category));
        when(organizerRepository.findAll()).thenReturn(List.of(organizer));

        when(eventMockFactory.createEvent(any(), any(), any())).thenReturn(event);
        when(eventRepository.save(event)).thenReturn(event);
        when(eventMapper.toDTO(event)).thenReturn(expectedDto);

        EventDto result = eventService.populateDatabase();

        assertEquals(expectedDto, result);
        verify(eventRepository).save(event);
    }

    @Test
    void testPopulateDatabaseNoAdmins() {
        when(adminRepository.findAll()).thenReturn(Collections.emptyList());
        when(categoryRepository.findAll()).thenReturn(List.of(new Category()));
        when(organizerRepository.findAll()).thenReturn(List.of(new Organizer()));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> eventService.populateDatabase());
        assertEquals("No admins found. Cannot create event.", ex.getMessage());
    }

    @Test
    void testPopulateDatabaseNoCategories() {
        when(adminRepository.findAll()).thenReturn(List.of(new Admin()));
        when(categoryRepository.findAll()).thenReturn(Collections.emptyList());
        when(organizerRepository.findAll()).thenReturn(List.of(new Organizer()));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> eventService.populateDatabase());
        assertEquals("No categories found. Cannot create event.", ex.getMessage());
    }

    @Test
    void testPopulateDatabaseNoOrganizers() {
        when(adminRepository.findAll()).thenReturn(List.of(new Admin()));
        when(categoryRepository.findAll()).thenReturn(List.of(new Category()));
        when(organizerRepository.findAll()).thenReturn(Collections.emptyList());

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> eventService.populateDatabase());
        assertEquals("No organizers found. Cannot create event.", ex.getMessage());
    }
}
