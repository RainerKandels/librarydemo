package de.kandels.librarydemo.entities;

import lombok.*;
import org.springframework.format.annotation.NumberFormat;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "publisher")
    private String publisher;

    @Column(name = "publishing_year", nullable = false)
    @NumberFormat(style = NumberFormat.Style.NUMBER)
    private Integer publishingYear;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id")
    private Category category;

}

