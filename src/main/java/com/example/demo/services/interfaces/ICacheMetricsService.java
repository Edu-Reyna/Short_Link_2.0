package com.example.demo.services.interfaces;

import com.example.demo.controller.dto.MetricsDTO;
import com.example.demo.entities.LinkEntity;
import com.example.demo.entities.MetricsEntity;

import java.util.List;

public interface ICacheMetricsService {

    void save(MetricsEntity metricsEntity);
    void saveAll(List<MetricsDTO> metricsDTOS);
    List<MetricsDTO> getMetricsByLinkId(Long linkId);

}
