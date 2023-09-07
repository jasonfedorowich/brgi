package com.bragi.bragi.rest.dto;

import com.bragi.bragi.model.Artist;
import com.bragi.bragi.model.Song;
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
public class Album {

    private long id;
    private UUID externalId;
    private String title;
    private Set<UUID> artists = new HashSet<>();
    private Set<UUID> songs = new HashSet<>();
    private Timestamp date;


    public static Album convertToDto(com.bragi.bragi.model.Album album) {
        return Album.builder()
                .title(album.getTitle())
                .date(album.getDate())
                .songs(album.getSongs()
                        .stream()
                        .map(Song::getExternalId)
                        .collect(Collectors.toSet()))
                .artists(album.getArtists()
                        .stream()
                        .map(Artist::getExternalId)
                        .collect(Collectors.toSet()))
                .build();
    }

}
