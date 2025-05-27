package com.example.demo.entities;

import com.example.demo.controller.dto.UserDTO;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

@RedisHash("Link")
@Data
public class CacheLinkEntity implements Serializable {


    @Id
    private Long id;

    private String title;

    private String url;

    @Indexed
    private String shortUrl;

    private Instant createdAt;

    private OffsetDateTime expiresAt;

    @Indexed
    private Long userId;

    @Indexed
    private boolean status;

    private List<CacheMetricsEntity> metrics;
}
