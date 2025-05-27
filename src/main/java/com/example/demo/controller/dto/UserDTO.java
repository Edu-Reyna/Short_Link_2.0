package com.example.demo.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    @Id
    private Long id;
    private String name;
    private String email;
}
