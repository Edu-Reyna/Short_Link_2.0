package com.example.demo.controller.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LinkDTO {

    private Long id;
    private String title;
    private @NotBlank String url;
    private String shortUrl;
    private Instant createdAt;
    private @Nullable OffsetDateTime expiresAt;
    private @Nullable UserDTO user;

    @JsonManagedReference
    private @Nullable List<MetricsDTO> metrics;
    private boolean status;

}
