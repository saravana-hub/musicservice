package com.plugsurfing.musicservice.api.services.thirdParty;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.plugsurfing.musicservice.api.model.musicbrainz.MusicbrainzArtistDetails;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class MusicbrainzService {
    private final WebClient musicbrainzWebClient;

    @RateLimiter(name = "musicbrainz")
    public Publisher<MusicbrainzArtistDetails> getMusicbrainzArtistDetails(String mbid) {
        return musicbrainzWebClient
                .get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("artist/")
                                .path(mbid)
                                .queryParam("fmt", "json")
                                .queryParam("inc", "url-rels+release-groups")
                                .build())
                .accept(APPLICATION_JSON)
                .retrieve()
                .bodyToMono(MusicbrainzArtistDetails.class);
    }
}
