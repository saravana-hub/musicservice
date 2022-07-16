package com.plugsurfing.musicservice.api.services.thirdParty;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.micrometer.core.instrument.util.IOUtils;
import java.nio.charset.StandardCharsets;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class WikipediaThirdPartyServiceTest extends ThirdPartyServiceTestBase {
    private static final String RESOURCE_ID = "Q2831";
    private static final String TITLE = "Michael_Jackson";
    private static final String DESCRIPTION = "<p><b>Michael Joseph Jackson</b> was an American singer, songwriter, and dancer. Dubbed the \"King of Pop\", he is regarded as one of the most significant cultural figures of the 20th century. Over a four-decade career, his contributions to music, dance, and fashion, along with his publicized personal life, made him a global figure in popular culture. Jackson influenced artists across many music genres; through stage and video performances, he popularized complicated dance moves such as the moonwalk, to which he gave the name, as well as the robot. He is the most awarded individual music artist in history.</p>";
    private WikipediaService target;
    private final WikidataService wikidataService = mock(WikidataService.class);

    @BeforeEach
    void init() {
        WebClient wikipediaWebClient = WebClient.create("http://localhost:" + mockBackEnd.getPort());
        target  = new WikipediaService(wikidataService, wikipediaWebClient);
    }

    @Test
    public void shouldReturnValidWikipediaDescription() throws InterruptedException {
        mockBackEnd.enqueue(new MockResponse()
                .setBody(IOUtils.toString(WikipediaThirdPartyServiceTest.class.getResourceAsStream("/wikipediaResponse.json"), StandardCharsets.UTF_8))
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        );
        when(wikidataService.getWikipediaTitle(RESOURCE_ID)).thenReturn(Mono.just(TITLE));
        var result = target.getWikipediaDescription(RESOURCE_ID);

        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.equals(DESCRIPTION))
                .verifyComplete();

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();

        assertThat(recordedRequest.getMethod(), is("GET"));
        assertThat(recordedRequest.getPath(), is("/" + TITLE));
    }
}
