package com.example.demo;

import com.example.demo.controller.dto.LinkDTO;
import com.example.demo.controller.dto.UserDTO;
import com.example.demo.entities.LinkEntity;
import com.example.demo.entities.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.Instant;
import java.util.List;

public class DataProvider {

    public static List<LinkEntity> links(){
        return List.of(
                LinkEntity.builder()
                        .id(1L)
                        .url("https://www.google.com")
                        .shortUrl("abc123")
                        .user(new UserEntity())
                        .createdAt(Instant.now())
                        .status(true)
                        .build(),
                LinkEntity.builder()
                        .id(2L)
                        .url("https://www.example.com")
                        .shortUrl("def456")
                        .user(new UserEntity())
                        .createdAt(Instant.now())
                        .build()
        );
    }


    public static LinkDTO linkDTO(){
        return LinkDTO.builder()
                .id(1L)
                .url("https://www.google.com")
                .user(UserDTO.builder()
                        .id(1L)
                        .email("<EMAIL>")
                        .name("Eduardo")
                        .build())
                .createdAt(Instant.now())
                .shortUrl("abc123")
                .status(true)
                .build();
    };

    public static MockHttpServletRequest request(){
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setMethod("GET"); // Método HTTP
        request.setRequestURI("/links/shorten"); // URI de la solicitud
        request.setRemoteAddr("127.0.0.1"); // IP del cliente
        request.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36"); // User-Agent

        request.addParameter("customParam", "value");

        return request;

    }

}
