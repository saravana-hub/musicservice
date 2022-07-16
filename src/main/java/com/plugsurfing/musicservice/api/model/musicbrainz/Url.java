package com.plugsurfing.musicservice.api.model.musicbrainz;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Url(String resource) {
}
