package com.plugsurfing.musicservice.api.services;

import static com.plugsurfing.musicservice.api.services.thirdParty.CoverArtService.IMAGE_NOT_AVAILABLE;
import static com.plugsurfing.musicservice.api.services.thirdParty.CoverArtThirdPartyServiceTest.ID;
import static com.plugsurfing.musicservice.api.services.thirdParty.CoverArtThirdPartyServiceTest.TITLE;
import static java.util.List.of;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static reactor.core.publisher.Mono.just;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plugsurfing.musicservice.MusicserviceApplication;
import com.plugsurfing.musicservice.api.model.artist.Album;
import com.plugsurfing.musicservice.api.model.artist.ArtistDetails;
import com.plugsurfing.musicservice.api.model.coverArt.CoverArtResponse;
import com.plugsurfing.musicservice.api.model.musicbrainz.MusicbrainzArtistDetails;
import com.plugsurfing.musicservice.api.model.wikidata.WikiDataResponse;
import com.plugsurfing.musicservice.api.model.wikipedia.WikipediaResponse;
import com.plugsurfing.musicservice.api.services.thirdParty.CoverArtService;
import com.plugsurfing.musicservice.api.services.thirdParty.MusicbrainzService;
import com.plugsurfing.musicservice.api.services.thirdParty.WikidataService;
import com.plugsurfing.musicservice.api.services.thirdParty.WikipediaService;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {MusicserviceApplication.class})
public class ArtistServiceTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final Album ALBUM = new Album(ID, TITLE, IMAGE_NOT_AVAILABLE.image());
    private final WikipediaResponse wikipediaResponse = OBJECT_MAPPER.readValue(ArtistServiceTest.class.getResourceAsStream("/wikipediaResponse.json"), WikipediaResponse.class);
    private final MusicbrainzArtistDetails musicbrainzArtistDetails = OBJECT_MAPPER.readValue(ArtistServiceTest.class.getResourceAsStream("/musicbrainzResponse.json"), MusicbrainzArtistDetails.class);
    private final MusicbrainzService musicbrainzService = mock(MusicbrainzService.class);
    private final WikidataService wikidataService = mock(WikidataService.class);
    private final WikipediaService wikipediaService = mock(WikipediaService.class);
    private final CoverArtService coverArtService = mock(CoverArtService.class);
    private final ArtistService target = new ArtistService(musicbrainzService, coverArtService, wikipediaService);

    public ArtistServiceTest() throws IOException {
    }

    @Test
    public void shouldReturnValidArtistDetails() {
        when(musicbrainzService.getMusicbrainzArtistDetails(anyString())).thenReturn(just(musicbrainzArtistDetails));
        when(wikidataService.getWikipediaTitle(anyString())).thenReturn(just("Michael Jackson"));
        when(wikipediaService.getWikipediaDescription(anyString())).thenReturn(just(wikipediaResponse.extractHtml()));
        when(coverArtService.getAlbums(anyList())).thenReturn(just(of(ALBUM)));
        var result = target.getArtistDetails("dfs").block();

        assertThat(result, is(notNullValue()));
        assertThat(result.name(), is("Michael Jackson"));
        assertThat(result.country(), is("US"));
        assertThat(result.description(), is(wikipediaResponse.extractHtml()));
        assertThat(result.albums().size(), is(1));
        assertThat(result.albums().get(0), is(ALBUM));
    }
}
