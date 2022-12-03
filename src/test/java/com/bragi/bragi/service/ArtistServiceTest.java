package com.bragi.bragi.service;

import com.bragi.bragi.model.Album;
import com.bragi.bragi.model.Artist;
import com.bragi.bragi.model.Song;
import com.bragi.bragi.repository.ArtistRepository;
import com.bragi.bragi.service.config.RetryConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistServiceTest {

    private ArtistService artistService;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private RetryConfig retryConfig;

    @BeforeEach
    void setUp() {
        when(retryConfig.getMaxAttempts()).thenReturn(3);
        when(retryConfig.getMinWaitBetweenMillis()).thenReturn(100L);
        when(retryConfig.getMaxWaitBetweenMillis()).thenReturn(200L);

        artistService = new ArtistService(artistRepository, retryConfig);
    }

    @Test
    void when_store_success_thenReturns() {
        var artist = Artist.builder()
                .songs(new HashSet<>())
                .albums(new HashSet<>())
                .build();

        when(artistRepository.save(any())).thenReturn(artist);

        var newArtist = artistService.store(artist);

        assertEquals(artist, newArtist);
        verify(artistRepository).save(any());
    }

    @Test
    void when_store_failsThenRetries_thenReturns() {
        var artist = Artist.builder()
                .songs(new HashSet<>())
                .albums(new HashSet<>())
                .build();

        when(artistRepository.save(any()))
                .thenThrow(new RuntimeException())
                .thenReturn(artist);

        var newArtist = artistService.store(artist);

        assertEquals(artist, newArtist);
        verify(artistRepository, times(2)).save(any());
    }

    @Test
    void when_store_failsThenRetries_thenThrows() {
        var artist = Artist.builder()
                .songs(new HashSet<>())
                .albums(new HashSet<>())
                .build();

        when(artistRepository.save(any()))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, ()->{
            var newArtist = artistService.store(artist);
        });

        verify(artistRepository, times(3)).save(any());
    }

    @Test
    void when_getById_success_thenReturns() {
        var artist = Artist.builder()
                .songs(new HashSet<>())
                .albums(new HashSet<>())
                .build();

        when(artistRepository.findById(anyLong())).thenReturn(Optional.of(artist));

        var newArtist = artistService.getById(1L);

        assertEquals(artist, newArtist);
        verify(artistRepository).findById(anyLong());

    }

    @Test
    void when_getById_retries_thenReturns() {
        var artist = Artist.builder()
                .songs(new HashSet<>())
                .albums(new HashSet<>())
                .build();

        when(artistRepository.findById(anyLong()))
                .thenThrow(new RuntimeException())
                .thenReturn(Optional.of(artist));

        var newArtist = artistService.getById(1L);

        assertEquals(artist, newArtist);
        verify(artistRepository, times(2)).findById(anyLong());

    }

    @Test
    void when_getById_retries_thenThrows() {
        var artist = Artist.builder()
                .songs(new HashSet<>())
                .albums(new HashSet<>())
                .build();

        when(artistRepository.findById(anyLong()))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, ()->{
            var newArtist = artistService.getById(1L);

        });

        verify(artistRepository, times(3)).findById(anyLong());

    }

    @Test
    void when_deleteById_success_thenReturns() {
        assertDoesNotThrow(()->{
            artistService.deleteById(1L);
        });
        verify(artistRepository).deleteById(anyLong());
    }

    @Test
    void when_deleteById_retries_thenReturns() {
        doThrow(new RuntimeException())
                .doNothing()
                .when(artistRepository)
                .deleteById(anyLong());

        assertDoesNotThrow(()->{
            artistService.deleteById(1L);
        });
        verify(artistRepository, times(2)).deleteById(anyLong());
    }

    @Test
    void when_deleteById_retries_thenThrows() {
        doThrow(new RuntimeException())
                .when(artistRepository)
                .deleteById(anyLong());

        assertThrows(RuntimeException.class, ()->{
            artistService.deleteById(1L);
        });
        verify(artistRepository, times(3)).deleteById(anyLong());
    }

    @Test
    void when_getAllAlbums_success_thenReturns() {
        var albums = Set.of(Album.builder().title("hey").build());

        var artist = Artist.builder()
                .songs(new HashSet<>())
                .albums(albums)
                .build();

        when(artistRepository.findById(anyLong())).thenReturn(Optional.of(artist));

        var albums1 = artistService.getAllAlbums(1L);

        assertEquals(albums, albums1);

        verify(artistRepository).findById(anyLong());
    }

    @Test
    void when_getAllAlbums_retries_thenReturns() {
        var albums = Set.of(Album.builder().title("hey").build());

        var artist = Artist.builder()
                .songs(new HashSet<>())
                .albums(albums)
                .build();

        when(artistRepository.findById(anyLong()))
                .thenThrow(new RuntimeException())
                .thenReturn(Optional.of(artist));

        var albums1 = artistService.getAllAlbums(1L);

        assertEquals(albums, albums1);

        verify(artistRepository, times(2)).findById(anyLong());
    }

    @Test
    void when_getAllAlbums_retries_thenThrows() {
        var albums = Set.of(Album.builder().title("hey").build());

        var artist = Artist.builder()
                .songs(new HashSet<>())
                .albums(albums)
                .build();

        when(artistRepository.findById(anyLong()))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, ()->{
            var albums1 = artistService.getAllAlbums(1L);
        });

        verify(artistRepository, times(3)).findById(anyLong());
    }

    @Test
    void when_getAllSongs_success_thenReturns() {
        var songs = Set.of(Song.builder().title("hey").build());

        var artist = Artist.builder()
                .songs(songs)
                .albums(new HashSet<>())
                .build();

        when(artistRepository.findById(anyLong()))
                .thenReturn(Optional.of(artist));

        var songs1 = artistService.getAllSongs(1L);

        assertEquals(songs, songs1);

        verify(artistRepository).findById(anyLong());
    }


    @Test
    void when_getAllSongs_retries_thenReturns() {
        var songs = Set.of(Song.builder().title("hey").build());

        var artist = Artist.builder()
                .songs(songs)
                .albums(new HashSet<>())
                .build();

        when(artistRepository.findById(anyLong()))
                .thenThrow(new RuntimeException())
                .thenReturn(Optional.of(artist));

        var songs1 = artistService.getAllSongs(1L);

        assertEquals(songs, songs1);

        verify(artistRepository, times(2)).findById(anyLong());
    }

    @Test
    void when_getAllSongs_retries_thenThrows() {
        var songs = Set.of(Song.builder().title("hey").build());

        var artist = Artist.builder()
                .songs(songs)
                .albums(new HashSet<>())
                .build();

        when(artistRepository.findById(anyLong()))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, ()->{
            var songs1 = artistService.getAllSongs(1L);

        });

        verify(artistRepository, times(3)).findById(anyLong());
    }

    @Test
    void when_getArtistByName_success_thenReturns() {
        var artist = Artist.builder()
                .name("hello")
                .songs(new HashSet<>())
                .albums(new HashSet<>())
                .build();

        when(artistRepository.findByName(anyString())).thenReturn(Optional.of(artist));

        var artist1 = artistService.getArtistByName("hello");

        assertEquals(artist, artist1);

        verify(artistRepository).findByName(anyString());

    }

    @Test
    void when_getArtistByName_retries_thenReturns() {
        var artist = Artist.builder()
                .name("hello")
                .songs(new HashSet<>())
                .albums(new HashSet<>())
                .build();

        when(artistRepository.findByName(anyString()))
                .thenThrow(new RuntimeException())
                .thenReturn(Optional.of(artist));

        var artist1 = artistService.getArtistByName("hello");

        assertEquals(artist, artist1);

        verify(artistRepository, times(2)).findByName(anyString());

    }

    @Test
    void when_getArtistByName_retries_thenThrows() {
        var artist = Artist.builder()
                .name("hello")
                .songs(new HashSet<>())
                .albums(new HashSet<>())
                .build();

        when(artistRepository.findByName(anyString()))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, ()->{
            var artist1 = artistService.getArtistByName("hello");
        });


        verify(artistRepository, times(3)).findByName(anyString());

    }

}