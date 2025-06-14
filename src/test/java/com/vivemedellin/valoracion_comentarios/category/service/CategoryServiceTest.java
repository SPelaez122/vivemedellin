package com.vivemedellin.valoracion_comentarios.category.service;

import com.vivemedellin.valoracion_comentarios.category.dto.CategoryDTO;
import com.vivemedellin.valoracion_comentarios.category.entity.Category;
import com.vivemedellin.valoracion_comentarios.category.factory.CategoryMockFactory;
import com.vivemedellin.valoracion_comentarios.category.mapper.CategoryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    private CategoryRepository categoryRepository;
    private CategoryMapper categoryMapper;
    private CategoryMockFactory categoryMockFactory;
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryRepository = mock(CategoryRepository.class);
        categoryMapper = mock(CategoryMapper.class);
        categoryMockFactory = mock(CategoryMockFactory.class);
        categoryService = new CategoryService(categoryRepository, categoryMapper, categoryMockFactory);
    }

    @Test
    void testFindAll() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Cultura");
        category.setDescription("Eventos culturales");

        CategoryDTO dto = new CategoryDTO(1L, "Cultura", "Eventos culturales");

        when(categoryRepository.findAll()).thenReturn(List.of(category));
        when(categoryMapper.toDTO(category)).thenReturn(dto);

        List<CategoryDTO> result = categoryService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Cultura", result.get(0).getName());
        verify(categoryRepository).findAll();
        verify(categoryMapper).toDTO(category);
    }

    @Test
    void testPopulateDatabase() {
        Category category = new Category();
        category.setId(2L);
        category.setName("Tecnología");
        category.setDescription("Eventos de tecnología");

        CategoryDTO dto = new CategoryDTO(2L, "Tecnología", "Eventos de tecnología");

        when(categoryMockFactory.createMocks()).thenReturn(List.of(category));
        when(categoryRepository.saveAll(List.of(category))).thenReturn(List.of(category));
        when(categoryMapper.toDTO(category)).thenReturn(dto);

        List<CategoryDTO> result = categoryService.populateDatabase();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Tecnología", result.get(0).getName());
        verify(categoryMockFactory).createMocks();
        verify(categoryRepository).saveAll(List.of(category));
        verify(categoryMapper).toDTO(category);
    }
}
