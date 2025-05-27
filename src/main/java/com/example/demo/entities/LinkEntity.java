package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "links", indexes = {
    @Index(name = "idx_expires_at", columnList = "expires_at"),
    @Index(name = "idx_short_url", columnList = "short_url"),
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_status", columnList = "status")
})
public class LinkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "short_url", unique = true, nullable = false)
    private String shortUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;

    private boolean status;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private UserEntity user;

    @OneToMany(mappedBy = "link", fetch = FetchType.LAZY)
    private List<MetricsEntity> metrics = new ArrayList<>();
}
