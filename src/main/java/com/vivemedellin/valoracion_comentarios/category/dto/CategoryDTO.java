package com.vivemedellin.valoracion_comentarios.category.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class CategoryDTO implements Serializable {

    private Long id;
    private String name;
    private String description;
}
