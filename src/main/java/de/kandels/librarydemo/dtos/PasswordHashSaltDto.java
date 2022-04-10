package de.kandels.librarydemo.dtos;

import lombok.Data;

import java.io.Serializable;

@Data
public class PasswordHashSaltDto implements Serializable {
    private final byte[] passwordHash;
    private final byte[] passwordSalt;
}
