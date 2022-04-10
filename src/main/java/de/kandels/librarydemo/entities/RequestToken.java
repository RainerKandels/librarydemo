package de.kandels.librarydemo.entities;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Each customer gets one request-token via Login
 * It is reusable for one hour.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "request_token")
public class RequestToken {

    @Id
    @Column(name = "customer_id")
    private Long id;

    @Column(name = "token", nullable = false)
    private String requestToken;

    @Column(name = "creationTime", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "expirationTime", nullable = false)
    private LocalDateTime expirationTime;
}