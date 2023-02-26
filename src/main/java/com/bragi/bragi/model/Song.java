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
@Table(name = "song")
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "external_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID externalId;

    @Column(name = "title")
    private String title;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "album_id", referencedColumnName = "id")
    private Album album;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "song_artist",
            joinColumns = @JoinColumn(name = "id"),
            inverseJoinColumns = @JoinColumn(name= "id"))
    private Set<Artist> artists = new HashSet<>();

    public Set<Artist> getArtists(){
        if(artists == null){
            artists = new HashSet<>();
        }
        return artists;
    }

    @Column(name = "duration")
    private long duration;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "song_content_id", referencedColumnName = "id")
    private SongContent songContent;

    @Column(name = "date_released", nullable = false)
    private Timestamp dateReleased;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return id == song.id && duration == song.duration && Objects.equals(title, song.title) && Objects.equals(dateReleased, song.dateReleased);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, duration, dateReleased);
    }

    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", duration=" + duration +
                ", dateReleased=" + dateReleased +
                '}';
    }
}
