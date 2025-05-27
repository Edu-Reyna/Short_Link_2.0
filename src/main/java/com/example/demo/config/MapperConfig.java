package com.example.demo.config;

import com.example.demo.controller.dto.LinkDTO;
import com.example.demo.controller.dto.UserDTO;
import com.example.demo.entities.CacheLinkEntity;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public ModelMapper modelMapper() {

        return new ModelMapper();
    }
}
