package com.jstorr.pact_consumer.contract;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.*;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.jstorr.pact_consumer.service.dto.UsersDto;
import com.jstorr.pact_consumer.service.dto.User;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class creates a Pact between:
 *   Consumer: "pact-consumer"
 *   Provider: "UserProvider"
 */
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "UserProvider", port = "8091")
public class UserServicePactTest {

    /**
     * Define the Pact for GET /api/v1/user/users
     * (Renamed to avoid confusion with other interactions)
     */
    @Pact(consumer = "pact-consumer")
    public V4Pact createGetUsersPact(PactDslWithProvider builder) {
        return builder
                .given("A list of users exists")
                .uponReceiving("A request to GET all users")
                .path("/api/v1/user/users")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json")) // <-- Add this line
                .body("""
                {
                  "users": [
                    {
                      "name": "James Bond",
                      "email": "james.bond@007.com",
                      "userId": "123"
                    }
                  ]
                }
                """)
                .toPact(V4Pact.class);
    }

    /**
     * Define the Pact for POST /api/v1/user
     * (Renamed to avoid confusion with other interactions)
     */
    @Pact(consumer = "pact-consumer")
    public V4Pact createAddUserPact(PactDslWithProvider builder) {
        return builder
                .given("The provider can create a user")
                .uponReceiving("A request to create a new user")
                .path("/api/v1/user")
                .method("POST")
                .body("""
                {
                  "name": "James Bond",
                  "email": "james.bond@007.com"
                }
                """)
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json")) // <-- Add this line
                .body("""
                {
                  "name": "James Bond",
                  "email": "james.bond@007.com",
                  "userId": "789"
                }
                """)
                .toPact(V4Pact.class);
    }

    /**
     * Test method for GET /api/v1/user/users
     * References the 'createGetUsersPact' method
     */
    @Test
    @PactTestFor(pactMethod = "createGetUsersPact")
    void testGetUsers(MockServer mockServer) {
        RestAssured.baseURI = mockServer.getUrl();

        UsersDto result = RestAssured
                .given()
                .get("/api/v1/user/users")
                .then()
                .statusCode(200)
                .extract()
                .as(UsersDto.class);

        assertNotNull(result);
        assertNotNull(result.getUsers());
        assertFalse(result.getUsers().isEmpty());
        assertEquals("James Bond", result.getUsers().get(0).getName());
        assertEquals("james.bond@007.com", result.getUsers().get(0).getEmail());
        assertEquals("123", result.getUsers().get(0).getUserId());
    }

    /**
     * Test method for POST /api/v1/user
     * References the 'createAddUserPact' method
     */
    @Test
    @PactTestFor(pactMethod = "createAddUserPact")
    void testAddUser(MockServer mockServer) {
        RestAssured.baseURI = mockServer.getUrl();

        User result = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body("""
            {
              "name": "James Bond",
              "email": "james.bond@007.com"
            }
            """)
                .post("/api/v1/user")
                .then()
                .statusCode(200)
                .extract()
                .as(User.class);

        assertNotNull(result);
        assertEquals("James Bond", result.getName());
        assertEquals("james.bond@007.com", result.getEmail());
        assertEquals("789", result.getUserId());
    }
}