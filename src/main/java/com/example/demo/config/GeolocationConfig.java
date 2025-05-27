package com.example.demo.config;

import com.maxmind.geoip2.DatabaseReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;

@Configuration
public class GeolocationConfig {

    @Bean
    public DatabaseReader databaseReader() throws IOException {
        Resource resource = new ClassPathResource("geoIp/GeoLite2-Country.mmdb");
        File database = resource.getFile();
        return new DatabaseReader.Builder(database).build();
    }
}
