package de.kandels.librarydemo.dtos;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginCustomerDto implements Serializable {
    private final String email;
    private final String password;
}
