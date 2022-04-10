package de.kandels.librarydemo.dtos;

import lombok.Data;

import java.io.Serializable;

@Data
public class WriteCategoryDto implements Serializable {
    private final String name;
    private final String description;
    private final String requestToken;
}
