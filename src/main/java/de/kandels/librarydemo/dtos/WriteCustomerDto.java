package de.kandels.librarydemo.dtos;

import lombok.Data;

import java.io.Serializable;

@Data
public class WriteCustomerDto implements Serializable {
    private final String name;
    private final String email;
    private final String password;
    private final String requestToken;
}
