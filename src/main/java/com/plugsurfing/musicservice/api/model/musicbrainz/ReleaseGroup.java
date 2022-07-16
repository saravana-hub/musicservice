package com.plugsurfing.musicservice.api.model.musicbrainz;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ReleaseGroup(String id, String title, @JsonProperty("primary-type") String primaryType) {
}
