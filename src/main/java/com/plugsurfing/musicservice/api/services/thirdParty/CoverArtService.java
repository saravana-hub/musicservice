package com.plugsurfing.musicservice.api.services.thirdParty;

import com.plugsurfing.musicservice.api.model.artist.Album;
import com.plugsurfing.musicservice.api.model.coverArt.CoverArtResponse;
import com.plugsurfing.musicservice.api.model.coverArt.Image;
import com.plugsurfing.musicservice.api.model.musicbrainz.ReleaseGroup;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class CoverArtService {
    public static final Image IMAGE_NOT_AVAILABLE = new Image("Image not available", false);
    private final WebClient coverArtWebClient;

    public Publisher<List<Album>> getAlbums(List<ReleaseGroup> releaseGroup) {
        return Flux.fromStream(releaseGroup.stream())
                .flatMap(this::getAlbum)
                .collectList();
    }

    private Publisher<Album> getAlbum(ReleaseGroup releaseGroup) {
        var coverArtResponse = getCoverArtResponse(releaseGroup.id());
        return coverArtResponse.flatMap(response -> Mono.just(new Album(releaseGroup.id(), releaseGroup.title(), getImage(response))))
                .onErrorResume(e -> {
                    if (e instanceof WebClientResponseException.NotFound)
                        return Mono.just(new Album(releaseGroup.id(), releaseGroup.title(), IMAGE_NOT_AVAILABLE.image()));
                    else return Mono.error(e);
                });
    }

    private String getImage(CoverArtResponse response) {
        return response.images()
                .stream()
                .filter(Image::front)
                .findFirst()
                .orElse(response.images()
                        .stream()
                        .findFirst()
                        .orElse(IMAGE_NOT_AVAILABLE))
                .image();
    }

    private Mono<CoverArtResponse> getCoverArtResponse(String id) {
        return coverArtWebClient
                .get()
                .uri(id)
                .retrieve()
                .bodyToMono(CoverArtResponse.class);
    }
}
