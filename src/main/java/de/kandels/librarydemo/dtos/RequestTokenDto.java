package de.kandels.librarydemo.dtos;

import lombok.Data;

import java.io.Serializable;

@Data
public class RequestTokenDto implements Serializable {
    private final String requestToken;
}
