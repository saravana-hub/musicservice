package com.plugsurfing.musicservice.api.model.wikipedia;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WikipediaResponse(@JsonProperty("extract_html")String extractHtml) {
}
