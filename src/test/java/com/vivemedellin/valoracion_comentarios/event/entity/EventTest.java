package com.vivemedellin.valoracion_comentarios.event.entity;

import com.vivemedellin.valoracion_comentarios.admin.entity.Admin;
import com.vivemedellin.valoracion_comentarios.category.entity.Category;
import com.vivemedellin.valoracion_comentarios.organizer.entity.Organizer;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        Event event = new Event();

        Admin admin = new Admin();
        Organizer organizer = new Organizer();
        Category category = new Category();
        Instant now = Instant.now();
        BigDecimal price = new BigDecimal("50.00");

        event.setId(1L);
        event.setAdmin(admin);
        event.setOrganizer(organizer);
        event.setTitle("Concierto");
        event.setDescription("Concierto de rock");
        event.setDate(now);
        event.setLocation("Parque Principal");
        event.setCategory(category);
        event.setPrice(price);

        assertEquals(1L, event.getId());
        assertEquals(admin, event.getAdmin());
        assertEquals(organizer, event.getOrganizer());
        assertEquals("Concierto", event.getTitle());
        assertEquals("Concierto de rock", event.getDescription());
        assertEquals(now, event.getDate());
        assertEquals("Parque Principal", event.getLocation());
        assertEquals(category, event.getCategory());
        assertEquals(price, event.getPrice());
        assertNotNull(event.getReviews());
        assertTrue(event.getReviews().isEmpty());
    }

    @Test
    void testAllArgsConstructor() {
        Admin admin = new Admin();
        Organizer organizer = new Organizer();
        Category category = new Category();
        Instant now = Instant.now();
        BigDecimal price = new BigDecimal("75.00");

        Event event = new Event(
                2L,
                admin,
                List.of(), // reviews
                organizer,
                "Feria de las Flores",
                "Evento tradicional",
                now,
                "Medellín",
                category,
                price
        );

        assertEquals(2L, event.getId());
        assertEquals(admin, event.getAdmin());
        assertEquals(organizer, event.getOrganizer());
        assertEquals("Feria de las Flores", event.getTitle());
        assertEquals("Evento tradicional", event.getDescription());
        assertEquals(now, event.getDate());
        assertEquals("Medellín", event.getLocation());
        assertEquals(category, event.getCategory());
        assertEquals(price, event.getPrice());
        assertNotNull(event.getReviews());
        assertTrue(event.getReviews().isEmpty());
    }

    @Test
    void testBuilder() {
        Instant now = Instant.now();
        BigDecimal price = new BigDecimal("100.00");

        Event event = Event.builder()
                .title("Festival de Jazz")
                .description("Conciertos y talleres")
                .date(now)
                .location("Teatro Metropolitano")
                .price(price)
                .build();

        assertNull(event.getId());
        assertEquals("Festival de Jazz", event.getTitle());
        assertEquals("Conciertos y talleres", event.getDescription());
        assertEquals(now, event.getDate());
        assertEquals("Teatro Metropolitano", event.getLocation());
        assertEquals(price, event.getPrice());Add commentMore actions
    }
}
