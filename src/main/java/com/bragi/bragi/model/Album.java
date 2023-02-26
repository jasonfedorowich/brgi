package com.bragi.bragi.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.*;

@Entity
@Getter
@Setter
@Builder
@Table(name = "album")
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "external_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID externalId;

    @Column(name = "title", columnDefinition = "UNIQUE NON NULL")
    private String title;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "album_artist",
            joinColumns = @JoinColumn(name = "id"),
            inverseJoinColumns = @JoinColumn(name = "id"))
    private Set<Artist> artists = new HashSet<>();

    public Set<Artist> getArtists(){
        if(artists == null){
            artists = new HashSet<>();
        }
        return artists;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Song> songs = new HashSet<>();

    public Set<Song> getSongs(){
        if(songs == null){
            songs = new HashSet<>();
        }
        return songs;
    }

    @Column(name = "release_date", nullable = false)
    private Timestamp date;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Album album = (Album) o;
        return id == album.id && Objects.equals(title, album.title) && Objects.equals(date, album.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, date);
    }

    @Override
    public String toString() {
        return "Album{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", date=" + date +
                '}';
    }
}
