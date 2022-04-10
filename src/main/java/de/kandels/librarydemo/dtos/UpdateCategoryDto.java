package de.kandels.librarydemo.dtos;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateCategoryDto implements Serializable {
    private final Long id;
    private final String name;
    private final String description;
    private final String requestToken;
}
