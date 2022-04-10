package de.kandels.librarydemo.dtos;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateCustomerDto implements Serializable {
    private final Long id;
    private final String name;
    private final String email;
    private final String password;
    private final String requestToken;
}
