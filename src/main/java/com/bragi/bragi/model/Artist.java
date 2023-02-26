package com.bragi.bragi.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@Table(name = "artist")
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "external_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID externalId;

    @Column(name = "artist_name")
    private String name;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Song> songs = new HashSet<>();

    public Set<Song> getSongs(){
        if(songs == null){
            songs = new HashSet<>();
        }
        return songs;
    }

    @Column(name = "time_started", nullable = false)
    private Timestamp timeStarted;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Album> albums = new HashSet<>();

    public Set<Album> getAlbums(){
        if(albums == null){
            albums = new HashSet<>();
        }
        return albums;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Artist artist = (Artist) o;
        return id == artist.id && Objects.equals(name, artist.name) && Objects.equals(timeStarted, artist.timeStarted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, timeStarted);
    }

    @Override
    public String toString() {
        return "Artist{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", timeStarted=" + timeStarted +
                '}';
    }
}
