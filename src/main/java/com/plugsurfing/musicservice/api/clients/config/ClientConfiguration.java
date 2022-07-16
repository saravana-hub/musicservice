package com.plugsurfing.musicservice.api.clients.config;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ClientConfiguration {
    public static final int MAX_IN_MEMORY_SIZE = 16 * 1024 * 1024;
    @Value("${thirdParty.client.maxConnections:50}")
    private int maxConnections;

    @Value("${thirdParty.client.idleTime:1000}")
    private int idleTime;

    @Value("${thirdParty.client.lifeTime:3000}")
    private int lifeTime;

    @Value("${thirdParty.client.responseTimeout:10}")
    private int responseTimeOut;

    @Value("${thirdParty.client.baseUrl.musicbrainz}")
    private String musicbrainzBaseUrl;

    @Value("${thirdParty.client.baseUrl.wikidata}")
    private String wikidataBaseUrl;

    @Value("${thirdParty.client.baseUrl.wikipedia}")
    private String wikipediaBaseUrl;

    @Value("${thirdParty.client.baseUrl.coverArt}")
    private String coverArtBaseUrl;

    @Bean(name = "musicbrainzWebClient")
    public WebClient musicbrainzWebClient() {
        return createWebClient("musicbrainzWebClientBuilder", musicbrainzBaseUrl, maxConnections);
    }

    @Bean(name = "wikidataWebClient")
    public WebClient wikidataWebClient() {
        return createWebClient("wikidataWebClientBuilder", wikidataBaseUrl, maxConnections);
    }

    @Bean(name = "wikipediaWebClient")
    public WebClient wikipediaWebClient() {
        return createWebClient("wikipediaWebClientBuilder", wikipediaBaseUrl, maxConnections);
    }

    @Bean(name = "coverArtWebClient")
    public WebClient coverArtWebClient() {
        return createWebClient("coverArtWebClientBuilder", coverArtBaseUrl, maxConnections * 10);
    }

    private WebClient createWebClient(String name, String url, int maxConnections) {
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE))
                .build();
        ConnectionProvider connectionProvider =
                ConnectionProvider.builder(name)
                        .maxConnections(maxConnections)
                        .maxIdleTime(Duration.ofMillis(idleTime))
                        .maxLifeTime(Duration.ofMillis(lifeTime))
                        .build();
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create(connectionProvider)
                                .responseTimeout(Duration.ofSeconds(responseTimeOut))
                                .followRedirect(true)
                ))
                .baseUrl(url)
                .exchangeStrategies(strategies)
                .build();
    }
}
