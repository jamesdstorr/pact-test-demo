package com.jstorr.pact_consumer.service;

import com.jstorr.pact_consumer.service.dto.CreateUserDto;
import com.jstorr.pact_consumer.service.dto.User;
import com.jstorr.pact_consumer.service.dto.UsersDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserService {

    private final RestTemplate restTemplate;

    public UserService() {
        this.restTemplate = new RestTemplate();
    }

    public UsersDto getUsers(){
        return restTemplate.getForObject("http://localhost:8091/api/v1/user/users", UsersDto.class);
    }

    public User addUser(String name, String email){
        CreateUserDto dto = CreateUserDto.builder()
                .name("James Bond")
                .email("james.bond@007.com")
                .build();
        return restTemplate.postForObject("http://localhost:8091/api/v1/user",dto, User.class);
    }
}
