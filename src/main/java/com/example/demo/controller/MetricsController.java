package com.example.demo.controller;

import com.example.demo.controller.dto.MetricsDTO;
import com.example.demo.services.interfaces.IMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/metrics")
@RequiredArgsConstructor
public class MetricsController {

    private final IMetricsService metricsService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<MetricsDTO>> getMetrics(@PathVariable Long id){

        return new ResponseEntity<>(this.metricsService.getMetrics(id), HttpStatus.OK);
    }

}
