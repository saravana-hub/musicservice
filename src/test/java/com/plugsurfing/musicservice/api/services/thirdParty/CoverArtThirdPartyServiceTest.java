package com.plugsurfing.musicservice.api.services.thirdParty;

import static java.util.List.of;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.plugsurfing.musicservice.api.model.musicbrainz.ReleaseGroup;
import io.micrometer.core.instrument.util.IOUtils;
import java.nio.charset.StandardCharsets;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

public class CoverArtThirdPartyServiceTest extends ThirdPartyServiceTestBase {
    public static final String ID = "97e0014d-a267-33a0-a868-bb4e2552918a";
    public static final String TITLE = "Got to Be There";
    private CoverArtService target;

    @BeforeEach
    void init() {
        WebClient coverArtWebClient = WebClient.create("http://localhost:" + mockBackEnd.getPort());
        target  = new CoverArtService(coverArtWebClient);
    }

    @Test
    public void shouldReturnValidAlbum() throws InterruptedException {
        mockBackEnd.enqueue(new MockResponse()
                .setBody(IOUtils.toString(CoverArtThirdPartyServiceTest.class.getResourceAsStream("/coverArtResponse.json"), StandardCharsets.UTF_8))
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        );
        var result = target.getAlbums(of(new ReleaseGroup(ID, TITLE, "Album")));

        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.size() == 1 &&
                        response.get(0).id().equals(ID) &&
                        response.get(0).title().equals(TITLE) &&
                        response.get(0).imageUrl().equals("http://coverartarchive.org/release/51258fa4-29c5-4b86-bfda-b630573ec222/26167697750.jpg"))
                .verifyComplete();

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();

        assertThat(recordedRequest.getMethod(), is("GET"));
        assertThat(recordedRequest.getPath(), is("/" + ID));
    }

    @Test
    public void shouldReturnValidAlbumWithImageNotAvailable() throws InterruptedException {
        mockBackEnd.enqueue(new MockResponse()
                        .setResponseCode(HttpStatus.NOT_FOUND.value())
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        );
        var result = target.getAlbums(of(new ReleaseGroup(ID, TITLE, "Album")));

        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.size() == 1 &&
                                response.get(0).id().equals(ID) &&
                                response.get(0).title().equals(TITLE) &&
                                response.get(0).imageUrl().equals(CoverArtService.IMAGE_NOT_AVAILABLE.image()))
                .verifyComplete();

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();

        assertThat(recordedRequest.getMethod(), is("GET"));
        assertThat(recordedRequest.getPath(), is("/" + ID));
    }
}
