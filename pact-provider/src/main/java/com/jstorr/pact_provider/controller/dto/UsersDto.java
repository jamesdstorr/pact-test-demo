package com.jstorr.pact_provider.controller.dto;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class UsersDto {
    private List<User> users;

    public UsersDto() {
            this.users = Arrays.asList(
                    User.builder()
                            .name("John Doe")
                            .email("john.doe@email.com")
                            .userId("123")
                            .build(),
                    User.builder()
                            .name("Jane Doe")
                            .email("jane.doe@email.com")
                            .userId("456")
                            .build(),
                    User.builder()
                            .name("Jim Doe")
                            .email("jim.doe@email.com")
                            .userId("789")
                            .build()
            );
    }

}
