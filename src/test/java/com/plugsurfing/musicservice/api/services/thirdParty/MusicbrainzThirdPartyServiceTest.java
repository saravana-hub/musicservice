package com.plugsurfing.musicservice.api.services.thirdParty;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import io.micrometer.core.instrument.util.IOUtils;
import java.nio.charset.StandardCharsets;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

public class MusicbrainzThirdPartyServiceTest extends ThirdPartyServiceTestBase {

    private static final String MBID = "f27ec8db-af05-4f36-916e-3d57f91ecf5e";
    private MusicbrainzService target;

    @BeforeEach
    void init() {
        WebClient musicbrainzWebClient = WebClient.create("http://localhost:" + mockBackEnd.getPort());
        target  = new MusicbrainzService(musicbrainzWebClient);
    }

    @Test
    public void shouldReturnValidArtistDetails() throws InterruptedException {
        mockBackEnd.enqueue(new MockResponse()
                .setBody(IOUtils.toString(MusicbrainzThirdPartyServiceTest.class.getResourceAsStream("/musicbrainzResponse.json"), StandardCharsets.UTF_8))
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        );
        var result = target.getMusicbrainzArtistDetails(MBID);

        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.id().equals(MBID) &&
                        response.country().equals("US") &&
                        response.name().equals("Michael Jackson") &&
                        response.gender().equals("Male") &&
                        !response.relations().isEmpty() &&
                        !response.releaseGroups().isEmpty())
                .verifyComplete();

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();

        assertThat(recordedRequest.getMethod(), is("GET"));
        assertThat(recordedRequest.getPath(), is("/artist/" + MBID + "?fmt=json&inc=url-rels+release-groups"));
    }
}
