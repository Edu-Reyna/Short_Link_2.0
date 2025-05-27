package com.example.demo.repositories;

import com.example.demo.entities.MetricsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMetricsRepository extends JpaRepository<MetricsEntity, Long> {

    List<MetricsEntity> findAllByLink_Id(Long linkId);
}
