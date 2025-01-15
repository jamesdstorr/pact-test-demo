package com.jstorr.pact_provider.contract;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Provider("UserProvider")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@PactBroker(
        host = "localhost",
        port = "9292"
)
class PactProviderTest {

    @Value("${local.server.port}")
    private int serverPort;
    private HttpTestTarget target = new HttpTestTarget("localhost", serverPort);



    @State("The provider can create a user")
    public void setupCreateUser() {
        // Mock or prepare backend data for creating a user
        System.out.println("Setting up provider state: the provider can create a user");
    }

    @State("A list of users exists")
    public void setupListUsers() {
        // Mock or prepare backend data for fetching a list of users
        System.out.println("Setting up provider state: a list of users exists");
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @BeforeEach
    void setTarget(PactVerificationContext context) {
        context.setTarget(new HttpTestTarget("localhost", serverPort));
    }
}