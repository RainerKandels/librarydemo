package de.kandels.librarydemo.dtos;

import lombok.Data;

import java.io.Serializable;

@Data
public class ReadBookDto implements Serializable {
    private final Long id;
    private final String title;
    private final String author;
    private final String publisher;
    private final Integer publishingYear;
    private final Long categoryId;
    private final String categoryName;
}
