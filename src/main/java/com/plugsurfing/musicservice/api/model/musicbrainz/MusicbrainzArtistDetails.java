package com.plugsurfing.musicservice.api.model.musicbrainz;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MusicbrainzArtistDetails(String id, String name, String country, String gender, String disambiguation,
                                       List<Relation> relations,
                                       @JsonProperty("release-groups") List<ReleaseGroup> releaseGroups) {
}
