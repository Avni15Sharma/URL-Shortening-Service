package com.avni.URLShortener.DTO;

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
