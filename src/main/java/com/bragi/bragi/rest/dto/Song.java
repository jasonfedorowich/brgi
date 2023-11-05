package com.bragi.bragi.rest.dto;

import com.bragi.bragi.model.Album;
import com.bragi.bragi.model.Artist;
import com.bragi.bragi.model.SongContent;
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
public class Song {

    private long id;
    private UUID externalId;
    private String title;
    private UUID album;
    private Set<UUID> artists = new HashSet<>();
    private long duration;

    @Column(name = "date_released", nullable = false)
    private Timestamp dateReleased;

    public static Song convertToDto(com.bragi.bragi.model.Song song) {
        return Song.builder()
                .album(song.getAlbum().getExternalId())
                .artists(song.getArtists()
                        .stream()
                        .map(Artist::getExternalId)
                        .collect(Collectors.toSet()))
                .dateReleased(song.getDateReleased())
                .title(song.getTitle())
                .duration(song.getDuration())
                .id(song.getId())
                .externalId(song.getExternalId())
                .build();
    }
}
