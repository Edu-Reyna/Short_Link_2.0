package com.example.demo.controller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;

public record AuthCreateUserRequest(@NotBlank String email,
                                    @NotBlank String name,
                                    @NotBlank String password,
                                    @Max(1) Long idRole) {
}
