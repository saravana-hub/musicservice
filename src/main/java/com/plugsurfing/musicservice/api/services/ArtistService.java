package com.plugsurfing.musicservice.api.services;

import com.plugsurfing.musicservice.api.model.artist.ArtistDetails;
import com.plugsurfing.musicservice.api.model.musicbrainz.MusicbrainzArtistDetails;
import com.plugsurfing.musicservice.api.services.thirdParty.CoverArtService;
import com.plugsurfing.musicservice.api.services.thirdParty.MusicbrainzService;
import com.plugsurfing.musicservice.api.services.thirdParty.WikipediaService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "artists")
public class ArtistService {
    private final MusicbrainzService musicbrainzService;
    private final CoverArtService coverArtService;
    private final WikipediaService wikipediaService;

    @CircuitBreaker(name = "artistDetails")
    @TimeLimiter(name = "artistDetails")
    @Cacheable(cacheNames = "artists", key = "#mbid")
    public Mono<ArtistDetails> getArtistDetails(String mbid) {
        var musicbrainzArtistDetails = musicbrainzService.getMusicbrainzArtistDetails(mbid);

        return Mono.from(musicbrainzArtistDetails)
                .publishOn(Schedulers.boundedElastic())
                .flatMap(artistDetails -> {
                    var description = Mono.from(wikipediaService.getWikipediaDescription(getWikidataResourceId(artistDetails)));
                    var albums = Mono.from(coverArtService.getAlbums(artistDetails.releaseGroups()));

                    return Mono.zip(description, albums)
                            .map(tuple ->
                                    new ArtistDetails(artistDetails.id(), artistDetails.name(), artistDetails.gender(), artistDetails.country(), artistDetails.disambiguation(), tuple.getT1(), tuple.getT2()));
                });
    }

    private String getWikidataResourceId(MusicbrainzArtistDetails musicbrainzArtistDetails) {
        return musicbrainzArtistDetails.relations()
                .stream()
                .filter(relation -> relation.type().equals("wikidata"))
                .findFirst()
                .map(relation -> {
                    String[] tokens = relation.url().resource().split("/");
                    return tokens[tokens.length - 1];
                })
                .orElse("");
    }
}
