package com.plugsurfing.musicservice.api.model.coverArt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public record CoverArtResponse(List<Image> images) {
}
