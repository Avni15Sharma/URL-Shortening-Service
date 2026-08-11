package com.avni.URLShortener.DTO;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
public class GetURLStatsDTO {

    private Long id;

    private String url;

    private String shortCode;

    private Instant createdAt;

    private Instant updatedAt;

    private Long accessCount;
}
