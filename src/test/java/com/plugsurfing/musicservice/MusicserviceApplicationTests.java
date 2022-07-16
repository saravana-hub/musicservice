package com.plugsurfing.musicservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.plugsurfing.musicservice.api.exception.ServiceError;
import com.plugsurfing.musicservice.api.model.artist.ArtistDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class MusicserviceApplicationTests {
	@Autowired
	WebTestClient client;

	@Test
	public void getArtistDetails() {
		var response = client.get()
				.uri("http://localhost:8080/musify/music-artist/details/f27ec8db-af05-4f36-916e-3d57f91ecf5e")
				.exchange()
				.expectStatus()
				.isOk() // should return ok
				.expectBody(ArtistDetails.class)
				.returnResult()
				.getResponseBody();

		assertNotNull(response);
		var artistAlbums = response.albums();

		assertEquals("Michael Jackson", response.name());
		artistAlbums.forEach(album -> assertNotNull(album.imageUrl()));
	}

	@Test
	public void getArtistDetailsInvalidMbId() {
		var response = client.get()
				.uri("http://localhost:8080/musify/music-artist/details/invalid_mbid")
				.exchange()
				.expectStatus()
				.isBadRequest() // should return ok
				.expectBody(ServiceError.class)
				.returnResult()
				.getResponseBody();

		assertNotNull(response);
		assertEquals("400 BAD_REQUEST", response.errorMessage());
	}

	@Test
	public void getArtistDetailsNonExistentMbId() {
		var response = client.get()
				.uri("http://localhost:8080/musify/music-artist/details/1f9df192-a621-4f54-8850-2c5373b7eac0")
				.exchange()
				.expectStatus()
				.isNotFound() // should return ok
				.expectBody(ServiceError.class)
				.returnResult()
				.getResponseBody();

		assertNotNull(response);
		assertEquals("mbid does not exist", response.errorMessage());
	}

	@Test
	public void getArtistDetailsWhenImageNotAvailable() {
		var response = client.get()
				.uri("http://localhost:8080/musify/music-artist/details/1f9df192-a621-4f54-8850-2c5373b7eac9")
				.exchange()
				.expectStatus()
				.isOk() // should return ok
				.expectBody(ArtistDetails.class)
				.returnResult()
				.getResponseBody();

		assertNotNull(response);
		assertNotNull(response.name());
		assertEquals("Ludwig van Beethoven", response.name());

		var albums = response.albums();

		assertTrue(albums.stream().anyMatch(album -> album.imageUrl().equals("Image not available")));
	}

}
