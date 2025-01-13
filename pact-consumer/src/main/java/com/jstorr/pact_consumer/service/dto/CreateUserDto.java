package com.jstorr.pact_consumer.service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateUserDto {
    private String name;
    private String email;
}
