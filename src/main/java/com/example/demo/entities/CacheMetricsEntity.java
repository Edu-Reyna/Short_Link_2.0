package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.Instant;

@RedisHash("Metrics")
@Data
public class CacheMetricsEntity implements Serializable {

    @Id
    private Long id;

    private Instant clickedAt;

    private String ipAddress;

    private String userAgent;

    private String country;

    private String device;

    private String browser;

    private String os;

    private Long linkId;

}
