package com.jstorr.pact_consumer.service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private String name;
    private String email;
    private String userId;
}
