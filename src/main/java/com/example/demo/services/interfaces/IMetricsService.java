package com.example.demo.services.interfaces;

import com.example.demo.controller.dto.MetricsDTO;
import com.example.demo.entities.LinkEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface IMetricsService {

    List<MetricsDTO> getMetrics(Long id);
    LinkEntity createMetrics(LinkEntity linkEntity, HttpServletRequest request);
}
