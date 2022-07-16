package com.plugsurfing.musicservice.api.model.artist;

import java.util.List;

public record ArtistDetails(String mbid, String name, String gender, String country, String disambiguation, String description, List<Album> albums) {
}
