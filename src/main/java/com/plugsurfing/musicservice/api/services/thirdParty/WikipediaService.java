package com.plugsurfing.musicservice.api.services.thirdParty;

import com.plugsurfing.musicservice.api.model.wikipedia.WikipediaResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class WikipediaService {
    private final WikidataService wikidataService;
    private final WebClient wikipediaWebClient;

    public Publisher<String> getWikipediaDescription(String wikidataResourceId) {
        var wikipediaTitle = Mono.from(wikidataService.getWikipediaTitle(wikidataResourceId));
        return wikipediaTitle
                .flatMap(this::getWikipediaResponse)
                .map(WikipediaResponse::extractHtml);
    }

    private Mono<WikipediaResponse> getWikipediaResponse(String title) {
        return wikipediaWebClient
                .get()
                .uri(title)
                .retrieve()
                .bodyToMono(WikipediaResponse.class);
    }
}
