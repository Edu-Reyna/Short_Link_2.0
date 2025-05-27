package com.example.demo.repositories;

import com.example.demo.entities.CacheLinkEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ICacheLinkRepository extends CrudRepository<CacheLinkEntity, Long> {

    List<CacheLinkEntity> findCacheLinkEntitiesByUserIdAndStatus(Long userId, boolean status);

    Optional<CacheLinkEntity> findCacheLinkEntityByShortUrl(String shortUrl);

    Optional<CacheLinkEntity> findByShortUrl(String shortUrl);
}
