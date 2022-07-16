package com.plugsurfing.musicservice.api.model.wikidata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WikiDataResponse(Map<String, Entity> entities) {
}
