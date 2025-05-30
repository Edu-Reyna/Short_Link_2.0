package com.example.demo.repositories;

import com.example.demo.entities.CacheMetricsEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICacheMetricsRepository extends CrudRepository<CacheMetricsEntity, Long> {

    List<CacheMetricsEntity> findAllByLinkId(Long linkId);
}
