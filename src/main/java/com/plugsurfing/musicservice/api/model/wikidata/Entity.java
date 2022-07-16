package com.plugsurfing.musicservice.api.model.wikidata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;
@JsonIgnoreProperties(ignoreUnknown = true)
public record Entity(String id, Map<String, SiteLinks> sitelinks) {
}
