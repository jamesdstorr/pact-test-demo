package com.jstorr.pact_consumer.service.dto;

import lombok.Data;

import java.util.List;

@Data
public class UsersDto {
    private List<User> users;
}
