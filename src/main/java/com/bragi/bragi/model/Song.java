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

@Entity
@Getter
@Setter
@Builder
@Table(name = "song")
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "song_id")
    private long id;

    @Column(name = "title")
    private String title;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "album_id")
    private Album album;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "song_artist",
            joinColumns = @JoinColumn(name = "song_id"),
            inverseJoinColumns = @JoinColumn(name= "artist_id"))
    private Set<Artist> artists = new HashSet<>();

    @Column(name = "duration")
    private long duration;

    @Lob
    @Column(name = "song_content")
    private byte[] content;

    @Column
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
