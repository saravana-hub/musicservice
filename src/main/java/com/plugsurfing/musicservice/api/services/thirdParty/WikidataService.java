package com.plugsurfing.musicservice.api.services.thirdParty;

import com.plugsurfing.musicservice.api.model.wikidata.WikiDataResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class WikidataService {
    private final WebClient wikidataWebClient;

    public Publisher<String> getWikipediaTitle(String resourceId) {
         var wikidataResponseMono = getWikidataResponse(resourceId);
         return wikidataResponseMono
                 .map(response ->
                 {
                     String wikiUrl = response.entities().get(resourceId).sitelinks().get("enwiki").url();
                     return getWikiTitleFromUrl(wikiUrl);
                 });
    }

    private Mono<WikiDataResponse> getWikidataResponse(String resourceId) {
        return wikidataWebClient
                .get()
                .uri(resourceId + ".json")
                .retrieve()
                .bodyToMono(WikiDataResponse.class);
    }

    private String getWikiTitleFromUrl(String wikiUrl) {
        String[] tokens = wikiUrl.split("/");
        return tokens[tokens.length - 1];
    }
}
