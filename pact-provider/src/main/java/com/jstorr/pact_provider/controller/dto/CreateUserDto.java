package com.jstorr.pact_provider.controller.dto;

import lombok.Data;

@Data
public class CreateUserDto {
    private String name;
    private String email;
}
