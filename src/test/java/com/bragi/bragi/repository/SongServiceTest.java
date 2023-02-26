package com.bragi.bragi.repository;

import com.bragi.bragi.model.Album;
import com.bragi.bragi.model.Artist;
import com.bragi.bragi.model.Song;
import com.bragi.bragi.repository.AlbumRepository;
import com.bragi.bragi.repository.ArtistRepository;
import com.bragi.bragi.repository.DataAccessService;
import com.bragi.bragi.repository.SongRepository;
import com.bragi.bragi.service.config.RetryConfig;
import lombok.Data;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SongServiceTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private SongRepository songRepository;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private RetryConfig retryConfig;

    private DataAccessService songService;

    @BeforeEach
    void setUp() {
        when(retryConfig.getMaxAttempts()).thenReturn(3);
        when(retryConfig.getMinWaitBetweenMillis()).thenReturn(100L);
        when(retryConfig.getMaxWaitBetweenMillis()).thenReturn(200L);

        songService = new DataAccessService(retryConfig, albumRepository, artistRepository, songRepository);
    }

    @Test
    void when_store_success_thenReturns() {
        var song = Song.builder()
                .artists(new HashSet<>())
                .build();

        when(songRepository.save(any())).thenReturn(song);

        var newSong = songService.saveSong(song);

        assertEquals(song, newSong);
        verify(songRepository).save(any());
    }

    @Test
    void when_store_failsThenRetries_thenReturns() {
        var song = Song.builder()
                .artists(new HashSet<>())
                .build();

        when(songRepository.save(any()))
                .thenThrow(new RuntimeException())
                .thenReturn(song);

        var newSong = songService.saveSong(song);

        assertEquals(song, newSong);
        verify(songRepository, times(2)).save(any());
    }

    @Test
    void when_store_failsThenRetries_thenThrows() {
        var song = Song.builder()
                .artists(new HashSet<>())
                .build();

        when(songRepository.save(any()))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, ()->{
            var newSong = songService.saveSong(song);
        });

        verify(songRepository, times(3)).save(any());
    }

    @Test
    void when_getById_success_thenReturns() {
        var song = Song.builder()
                .artists(new HashSet<>())
                .build();

        when(songRepository.findById(anyLong())).thenReturn(Optional.of(song));

        var newSong = songService.getSongById(1L);

        assertEquals(song, newSong);
        verify(songRepository).findById(anyLong());

    }

    @Test
    void when_getById_retries_thenReturns() {
        var song = Song.builder()
                .artists(new HashSet<>())
                .build();

        when(songRepository.findById(anyLong()))
                .thenThrow(new RuntimeException())
                .thenReturn(Optional.of(song));

        var newSong = songService.getSongById(1L);

        assertEquals(song, newSong);
        verify(songRepository, times(2)).findById(anyLong());

    }

    @Test
    void when_getById_retries_thenThrows() {
        var song = Song.builder()
                .artists(new HashSet<>())
                .build();

        when(songRepository.findById(anyLong()))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, ()->{
            var newArtist = songService.getSongById(1L);

        });

        verify(songRepository, times(3)).findById(anyLong());

    }

    @Test
    void when_deleteById_success_thenReturns() {
        assertDoesNotThrow(()->{
            songService.deleteSongById(1L);
        });
        verify(songRepository).deleteById(anyLong());
    }

    @Test
    void when_deleteById_retries_thenReturns() {
        doThrow(new RuntimeException())
                .doNothing()
                .when(songRepository)
                .deleteById(anyLong());

        assertDoesNotThrow(()->{
            songService.deleteSongById(1L);
        });
        verify(songRepository, times(2)).deleteById(anyLong());
    }

    @Test
    void when_deleteById_retries_thenThrows() {
        doThrow(new RuntimeException())
                .when(songRepository)
                .deleteById(anyLong());

        assertThrows(RuntimeException.class, ()->{
            songService.deleteSongById(1L);
        });
        verify(songRepository, times(3)).deleteById(anyLong());
    }

    @Test
    void when_getAllAlbum_success_thenReturns() {
        var album = Album.builder().title("hey").build();

        var song = Song.builder()
                .artists(new HashSet<>())
                .album(album)
                .build();

        when(songRepository.findById(anyLong())).thenReturn(Optional.of(song));

        var album1 = songService.getAlbumBySongId(1L);

        assertEquals(album, album1);

        verify(songRepository).findById(anyLong());
    }

    @Test
    void when_getAllAlbum_retries_thenReturns() {
        var album = Album.builder().title("hey").build();

        var song = Song.builder()
                .artists(new HashSet<>())
                .album(album)
                .build();

        when(songRepository.findById(anyLong()))
                .thenThrow(new RuntimeException())
                .thenReturn(Optional.of(song));

        var albums1 = songService.getAlbumBySongId(1L);

        assertEquals(album, albums1);

        verify(songRepository, times(2)).findById(anyLong());
    }

    @Test
    void when_getAllAlbums_retries_thenThrows() {
        var album = Album.builder().title("hey").build();

        var song = Song.builder()
                .artists(new HashSet<>())
                .album(album)
                .build();

        when(songRepository.findById(anyLong()))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, ()->{
            var albums1 = songService.getAlbumBySongId(1L);
        });

        verify(songRepository, times(3)).findById(anyLong());
    }

    @Test
    void when_getAllArtists_success_thenReturns() {
        var album = Album.builder().title("hey").build();
        var artists = Set.of(Artist.builder()
                .name("jason").build(), Artist.builder().name("hello").build());

        var song = Song.builder()
                .artists(artists)
                .album(album)
                .build();

        when(songRepository.findById(anyLong()))
                .thenReturn(Optional.of(song));

        var artists1 = songService.getAllArtistsFromSongId(1L);

        assertEquals(artists, artists1);

        verify(songRepository).findById(anyLong());
    }


    @Test
    void when_getAllArtists_retries_thenReturns() {
        var album = Album.builder().title("hey").build();
        var artists = Set.of(Artist.builder()
                .name("jason").build(), Artist.builder().name("hello").build());

        var song = Song.builder()
                .artists(artists)
                .album(album)
                .build();

        when(songRepository.findById(anyLong()))
                .thenThrow(new RuntimeException())
                .thenReturn(Optional.of(song));

        var artists1 = songService.getAllArtistsFromSongId(1L);

        assertEquals(artists, artists1);

        verify(songRepository, times(2)).findById(anyLong());
    }

    @Test
    void when_getAllArtists_retries_thenThrows() {
        var album = Album.builder().title("hey").build();
        var artists = Set.of(Artist.builder()
                .name("jason").build(), Artist.builder().name("hello").build());

        var song = Song.builder()
                .artists(artists)
                .album(album)
                .build();

        when(songRepository.findById(anyLong()))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, ()->{
            var songs1 = songService.getAllArtistsFromSongId(1L);

        });

        verify(songRepository, times(3)).findById(anyLong());
    }

    @Test
    void when_getSongByName_success_thenReturns() {
        var album = Album.builder().title("hey").build();
        var artists = Set.of(Artist.builder()
                .name("jason").build(), Artist.builder().name("hello").build());

        var song = Song.builder()
                .artists(artists)
                .album(album)
                .build();

        when(songRepository.findByTitle(anyString())).thenReturn(Optional.of(song));

        var song1 = songService.getSongByName("hello");

        assertEquals(song, song1);

        verify(songRepository).findByTitle(anyString());

    }

    @Test
    void when_getArtistByName_retries_thenReturns() {
        var album = Album.builder().title("hey").build();
        var artists = Set.of(Artist.builder()
                .name("jason").build(), Artist.builder().name("hello").build());

        var song = Song.builder()
                .artists(artists)
                .album(album)
                .build();

        when(songRepository.findByTitle(anyString()))
                .thenThrow(new RuntimeException())
                .thenReturn(Optional.of(song));

        var song1 = songService.getSongByName("hello");

        assertEquals(song, song1);

        verify(songRepository, times(2)).findByTitle(anyString());

    }

    @Test
    void when_getArtistByName_retries_thenThrows() {
        var album = Album.builder().title("hey").build();
        var artists = Set.of(Artist.builder()
                .name("jason").build(), Artist.builder().name("hello").build());

        var song = Song.builder()
                .artists(artists)
                .album(album)
                .build();


        when(songRepository.findByTitle(anyString()))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, ()->{
            var artist1 = songService.getSongByName("hello");
        });


        verify(songRepository, times(3)).findByTitle(anyString());

    }

    @Test
    void when_getSongByExternalId_success_thenReturns() {
        var album = Album.builder().title("hey").build();
        var artists = Set.of(Artist.builder()
                .name("jason").build(), Artist.builder().name("hello").build());

        var song = Song.builder()
                .artists(artists)
                .album(album)
                .build();

        when(songRepository.findByExternalId(any())).thenReturn(Optional.of(song));

        var song1 = songService.getSongByExternalId(UUID.randomUUID());

        assertEquals(song, song1);

        verify(songRepository).findByExternalId(any());

    }

    @Test
    void when_getByExternalId_retries_thenReturns() {
        var album = Album.builder().title("hey").build();
        var artists = Set.of(Artist.builder()
                .name("jason").build(), Artist.builder().name("hello").build());

        var song = Song.builder()
                .artists(artists)
                .album(album)
                .build();

        when(songRepository.findByExternalId(any()))
                .thenThrow(new RuntimeException())
                .thenReturn(Optional.of(song));

        var song1 = songService.getSongByExternalId(UUID.randomUUID());

        assertEquals(song, song1);

        verify(songRepository, times(2)).findByExternalId(any());

    }

    @Test
    void when_getByExternalId_retries_thenThrows() {
        var album = Album.builder().title("hey").build();
        var artists = Set.of(Artist.builder()
                .name("jason").build(), Artist.builder().name("hello").build());

        var song = Song.builder()
                .artists(artists)
                .album(album)
                .build();


        when(songRepository.findByExternalId(any()))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, ()->{
            var artist1 = songService.getSongByExternalId(UUID.randomUUID());
        });


        verify(songRepository, times(3)).findByExternalId(any());

    }


}