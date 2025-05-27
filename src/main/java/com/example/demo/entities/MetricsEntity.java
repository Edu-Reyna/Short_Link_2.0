package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "metrics", indexes = {
        @Index(name = "idx_link_id", columnList = "link_id"),
        @Index(name = "idx_clicked_at", columnList = "clicked_at")
})
public class MetricsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "clicked_at", nullable = false)
    private Instant clickedAt;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    private String country;

    private String device;

    private String browser;

    private String os;

    @ManyToOne
    @JoinColumn(name = "link_id")
    private LinkEntity link;
}
