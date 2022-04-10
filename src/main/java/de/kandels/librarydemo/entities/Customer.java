package de.kandels.librarydemo.entities;

import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @ToString.Exclude
    @Lob
    @Column(name = "password_hash", nullable = false)
    private byte[] passwordHash;

    @ToString.Exclude
    @Lob
    @Column(name = "password_salt", nullable = false)
    private byte[] passwordSalt;

}
