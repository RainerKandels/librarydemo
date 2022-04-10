package de.kandels.librarydemo.dtos;

import lombok.Data;

import java.io.Serializable;

@Data
public class IdAndTokenDto implements Serializable {
    private final Long id;
    private final String requestToken;
}
