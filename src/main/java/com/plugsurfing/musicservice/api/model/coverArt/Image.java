package com.plugsurfing.musicservice.api.model.coverArt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Image(String image, boolean front) {
}
