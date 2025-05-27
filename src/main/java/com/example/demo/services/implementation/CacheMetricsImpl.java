package com.example.demo.services.implementation;

import com.example.demo.controller.dto.MetricsDTO;
import com.example.demo.entities.CacheMetricsEntity;
import com.example.demo.entities.LinkEntity;
import com.example.demo.entities.MetricsEntity;
import com.example.demo.repositories.ICacheMetricsRepository;
import com.example.demo.services.interfaces.ICacheMetricsService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CacheMetricsImpl implements ICacheMetricsService {

    private final ICacheMetricsRepository cacheMetricsRepository;
    private final ModelMapper modelMapper;

    @Override
    public void save(MetricsEntity metricsEntity) {
        CacheMetricsEntity cacheMetricsEntity = this.modelMapper.map(metricsEntity, CacheMetricsEntity.class);
        this.cacheMetricsRepository.save(cacheMetricsEntity);
    }

    @Override
    public void saveAll(List<MetricsDTO> metricsDTOS) {
        List<CacheMetricsEntity> cacheMetricsEntities = metricsDTOS.stream()
                .map(metricsDTO -> this.modelMapper.map(metricsDTO, CacheMetricsEntity.class))
                .toList();
        this.cacheMetricsRepository.saveAll(cacheMetricsEntities);
    }

    @Override
    public List<MetricsDTO> getMetricsByLinkId(Long linkId) {
        return this.cacheMetricsRepository.findAllByLinkId(linkId).stream()
                .map(metricsEntity -> this.modelMapper.map(metricsEntity, MetricsDTO.class))
                .toList();
    }
}
