# Pact Testing with Java

## Introduction to Pact Testing

**Pact** is a consumer-driven contract testing tool used to ensure interactions between a consumer and a provider remain compatible. Instead of testing live interactions, Pact tests the contract, which is the agreed-upon format of requests and responses between the systems.

---

## How Pact Works

1. **Consumer Contract Tests:** The consumer defines its expectations (requests it will send and responses it expects) using Pact DSL.
2. **Contract Generation:** Based on these tests, a contract is generated in the form of a JSON file.
3. **Publishing the Contract:** The contract is published to a Pact Broker, a central repository for managing and sharing contracts.
4. **Provider Verification:** The provider tests against the contract to ensure it adheres to the expectations set by the consumer.
5. **Continuous Feedback:** If the provider fails to meet the contract, it updates its API or negotiates changes with the consumer.

---
## Set up the Pact Broker
The Pact Broker is a central repository for managing and sharing contracts between consumers and providers. It enables seamless collaboration and continuous feedback during integration testing

1. Install Docker
2. Run the following command to deploy and start the Pact Broker Container along with the Postgres Container:
```bash
docker-compose up -d
```
3. Open your browser and navigate to http://localhost:9292


## Setting Up Consumer Tests

### 1. **Consumer Pact Test Structure**
Consumer tests are written using Pact DSL. For this project, we used the JUnit 5 integration with Pact.

### 2. **Example Test Setup**

#### Pact for `GET /api/v1/user/users`:
```java
@Pact(consumer = "pact-consumer")
public V4Pact createGetUsersPact(PactDslWithProvider builder) {
    return builder
        .given("A list of users exists")
        .uponReceiving("A request to GET all users")
        .path("/api/v1/user/users")
        .method("GET")
        .willRespondWith()
        .status(200)
        .headers(Map.of("Content-Type", "application/json"))
        .body(
            new PactDslJsonBody()
                .minArrayLike("users", 1)
                .stringType("name")
                .stringMatcher("email", ".+@.+\\..+", "example@example.com")
                .stringType("userId")
        )
        .toPact(V4Pact.class);
}
```

#### Pact for `POST /api/v1/user`:
```java
@Pact(consumer = "pact-consumer")
public V4Pact createAddUserPact(PactDslWithProvider builder) {
    return builder
        .given("The provider can create a user")
        .uponReceiving("A request to create a new user")
        .path("/api/v1/user")
        .method("POST")
        .body(
            """
            {
              "name": "James Bond",
              "email": "james.bond@007.com"
            }
            """
        )
        .willRespondWith()
        .status(201)
        .headers(Map.of("Content-Type", "application/json"))
        .body(
            new PactDslJsonBody()
                .stringType("name")
                .stringMatcher("email", ".+@.+\\..+", "example@example.com")
                .stringType("userId")
        )
        .toPact(V4Pact.class);
}
```

### 3. **Including the Tests to Generate Contracts**

Ensure the tests are executed to generate the contracts. For example:

```java
@Test
@PactTestFor(pactMethod = "createGetUsersPact")
void testGetUsers(MockServer mockServer) {
    RestAssured.baseURI = mockServer.getUrl();
    // Add test implementation
}

@Test
@PactTestFor(pactMethod = "createAddUserPact")
void testAddUser(MockServer mockServer) {
    RestAssured.baseURI = mockServer.getUrl();
    // Add test implementation
}
```

### 4. **Running Consumer Tests**
Run the tests using Maven to generate the contract:
```bash
mvn clean install
```

If the tests pass, a contract file (JSON) will be generated in the `target/pacts` directory.

---

## Publishing the Contract

After generating the contract, it needs to be published to the Pact Broker for the provider to validate.

### 1. **Using Maven Plugin for Publishing**
Add the Pact Maven plugin to your `pom.xml`:
```xml
<plugin>
    <groupId>au.com.dius.pact</groupId>
    <artifactId>pact-jvm-provider-maven</artifactId>
    <version>4.5.x</version>
    <executions>
        <execution>
            <goals>
                <goal>publish</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <pactBrokerUrl>http://your-pact-broker-url</pactBrokerUrl>
        <tags>
            <tag>dev</tag>
        </tags>
    </configuration>
</plugin>
```

Run the following command to publish:
```bash
mvn pact:publish
```

### 2. **Contract in the Pact Broker**
Once published, the contract is available in the Pact Broker, where it can be shared and validated by the provider.

---

## Validating the Contract with the Provider

### 1. **Setting Up Provider Verification**
The provider uses the contract from the Pact Broker to validate its API implementation.

#### Example Verification Setup:
```java
@Provider("UserProvider")
@PactBroker(url = "http://your-pact-broker-url")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class PactProviderTest {

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @State("A list of users exists")
    public void toGetUsersState() {
        // Mock or set up data for this state
    }

    @State("The provider can create a user")
    public void toAddUserState() {
        // Mock or set up data for this state
    }
}
```

### 2. **Running the Provider Tests**
Run the provider tests using Maven:
```bash
mvn clean verify
```

### 3. **Validation Results**
- If the provider meets the contract expectations, the tests pass.
- If the provider does not meet the contract, errors are reported, specifying which parts of the contract failed.

---

## Summary Workflow

1. **Consumer:** Write and run Pact tests to define expectations.
2. **Generate Contract:** The consumer generates a contract JSON file by executing the tests.
3. **Publish Contract:** The contract is uploaded to the Pact Broker.
4. **Provider Verification:** The provider validates the contract by running tests against its API.
5. **Continuous Feedback:** Resolve mismatches iteratively for compatibility.


