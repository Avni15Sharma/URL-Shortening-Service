package com.avni.URLShortener.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class ErrorResponseDTO {
    private String message;
    private int httpStatusCode;
    private Instant timestamp;
}
