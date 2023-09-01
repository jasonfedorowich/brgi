package com.bragi.bragi.rest.dto;

import com.bragi.bragi.model.Album;
import com.bragi.bragi.model.Song;
import com.bragi.bragi.service.ArtistService;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Builder
@Getter
@Setter
@EqualsAndHashCode
public class Artist {

    private UUID externalId;
    private long id;
    private String name;
    private Set<UUID> songs = new HashSet<>();
    private Timestamp timeStarted;
    private Set<UUID> albums = new HashSet<>();

    public static Artist convertToDto(com.bragi.bragi.model.Artist artist){
        return Artist.builder()
                .name(artist.getName())
                .externalId(artist.getExternalId())
                .timeStarted(artist.getTimeStarted())
                .id(artist.getId())
                .albums(artist.getAlbums()
                        .stream()
                        .map(Album::getExternalId)
                        .collect(Collectors
                                .toSet()))
                .songs(artist.getSongs()
                        .stream()
                        .map(Song::getExternalId)
                        .collect(Collectors.toSet()))
                .build();
    }


}
