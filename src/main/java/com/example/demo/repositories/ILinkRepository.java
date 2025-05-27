package com.example.demo.repositories;

import com.example.demo.entities.LinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ILinkRepository extends JpaRepository<LinkEntity, Long> {
   List<LinkEntity> findAllByUserIdAndStatus(Long userId, boolean status);

    boolean existsByShortUrl(String shortUrl);

    Optional<LinkEntity> findByShortUrl(String shortUrl);

    List<LinkEntity> findLinkEntitiesByStatusAndExpiresAtBefore(boolean status, OffsetDateTime expiresAtBefore);

}
