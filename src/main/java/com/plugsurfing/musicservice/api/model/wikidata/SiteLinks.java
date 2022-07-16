package com.plugsurfing.musicservice.api.model.wikidata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SiteLinks(String site, String title, String url) {
}
