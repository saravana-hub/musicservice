package com.plugsurfing.musicservice.api.services.thirdParty;

import static com.plugsurfing.musicservice.api.clients.config.ClientConfiguration.MAX_IN_MEMORY_SIZE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import io.micrometer.core.instrument.util.IOUtils;
import java.nio.charset.StandardCharsets;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

public class WikidataThirdPartyServiceTest extends ThirdPartyServiceTestBase {
    private static final String RESOURCE_ID = "Q2831";
    private WikidataService target;

    @BeforeEach
    void init() {
        WebClient wikidataWebClient = WebClient
                                        .builder()
                                        .baseUrl("http://localhost:" + mockBackEnd.getPort())
                                        .exchangeStrategies(
                                                ExchangeStrategies
                                                        .builder()
                                                        .codecs(codes-> codes
                                                                .defaultCodecs()
                                                                .maxInMemorySize(MAX_IN_MEMORY_SIZE))
                                                        .build())
                                        .build();
        target  = new WikidataService(wikidataWebClient);
    }

    @Test
    public void shouldReturnValidWikipediaTitle() throws InterruptedException {
        mockBackEnd.enqueue(new MockResponse()
                .setBody(IOUtils.toString(WikidataThirdPartyServiceTest.class.getResourceAsStream("/wikidataResponse.json"), StandardCharsets.UTF_8))
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        );
        var result = target.getWikipediaTitle(RESOURCE_ID);

        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.equals("Michael_Jackson"))
                .verifyComplete();

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();

        assertThat(recordedRequest.getMethod(), is("GET"));
        assertThat(recordedRequest.getPath(), is("/" + RESOURCE_ID + ".json"));
    }
}
