package com.example.demo.controller.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MetricsDTO {
    private Long id;
    private Instant clickedAt;
    private String ipAddress;
    private String userAgent;
    private String country;
    private String device;
    private String browser;
    private String os;

    @JsonBackReference
    private LinkDTO link;
}
