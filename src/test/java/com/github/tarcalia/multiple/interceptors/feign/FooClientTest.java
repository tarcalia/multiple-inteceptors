package com.github.tarcalia.multiple.interceptors.feign;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.TestPropertySource;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@TestPropertySource(properties = {
        "foo.base-url=http://localhost:${wiremock.server.port}"
})
class FooClientTest {

    @Autowired
    FooClient fooClient;

    @Test
    void shouldSendAuthTokenHeader() {
        // Given
        stubFor(get(urlEqualTo("/api/internal/foo"))
                .willReturn(aResponse().withStatus(200).withBody("OK")));

        // When
        fooClient.callInternalFoo();

        // Then
        verify(getRequestedFor(urlEqualTo("/api/internal/foo"))
                .withHeader("X-Auth-Token", matching("mockedTokenValue"))
                .withHeader("X-Tracking-ID", matching("[0-9a-fA-F\\-]{36}"))
                .withHeader("X-Library", matching("libValue")));
    }
}