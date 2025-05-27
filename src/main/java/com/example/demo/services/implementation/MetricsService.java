package com.example.demo.services.implementation;

import com.example.demo.controller.dto.MetricsDTO;
import com.example.demo.entities.CacheLinkEntity;
import com.example.demo.entities.LinkEntity;
import com.example.demo.entities.MetricsEntity;
import com.example.demo.repositories.IMetricsRepository;
import com.example.demo.services.interfaces.IMetricsService;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.record.Country;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ua_parser.Parser;
import ua_parser.Client;

import java.io.IOException;
import java.net.InetAddress;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetricsService implements IMetricsService {

    private final DatabaseReader databaseReader;
    private final IMetricsRepository metricsRepository;
    private final CacheMetricsImpl cacheMetricsImpl;
    private final ModelMapper modelMapper;

    @Override
    public List<MetricsDTO> getMetrics(Long id) {
        List<MetricsDTO> metrics = this.cacheMetricsImpl.getMetricsByLinkId(id);

        if (!metrics.isEmpty()) {
            log.info("Found metrics in redis", metrics.size());
            return metrics;
        }
        metrics = this.metricsRepository.findAllByLink_Id(id)
                .stream()
                .map(metricsEntity -> this.modelMapper.map(metricsEntity, MetricsDTO.class))
                .toList();

        if (!metrics.isEmpty()) {
           this.cacheMetricsImpl.saveAll(metrics);
        }
        log.info("Found metrics in database", metrics.size());

        return metrics;
    }

    @Override
    public LinkEntity createMetrics(LinkEntity linkEntity, HttpServletRequest request) {
        MetricsEntity metricsEntity = new MetricsEntity();

        metricsEntity.setLink(linkEntity);
        metricsEntity.setClickedAt(Instant.now());

        String clientIp = extractClientIp(request);
        metricsEntity.setIpAddress(clientIp);

        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null) {
            metricsEntity.setUserAgent(userAgent);
        }
        List<String> deviceInfo = extractDeviceInfo(userAgent);
        metricsEntity.setOs(deviceInfo.get(0));
        metricsEntity.setBrowser(deviceInfo.get(1));
        metricsEntity.setDevice(deviceInfo.get(2));

        String country = extractCountry(clientIp);
        metricsEntity.setCountry(country);

        this.metricsRepository.save(metricsEntity);
        this.cacheMetricsImpl.save(metricsEntity);

        if (linkEntity.getMetrics() == null) {
            linkEntity.setMetrics(new ArrayList<>());
        }
        linkEntity.getMetrics().add(metricsEntity);

        return linkEntity;

    }

    private String extractClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private List<String> extractDeviceInfo(String userAgent) {

        Parser uaParser = new Parser();
        Client c = uaParser.parse(userAgent);
        String os = c.os.family;
        String browser = c.userAgent.family;
        String deviceType = c.device.family;

        return List.of(os, browser, deviceType);
    }

    private String extractCountry(String ipAddress) {
        try {
            InetAddress ip = InetAddress.getByName(ipAddress);
            return databaseReader.country(ip)
                    .getCountry().getName();
        } catch (GeoIp2Exception | IOException e) {
            return "Unknown";
        }
    }
}
