package com.bragi.bragi.service;

import com.bragi.bragi.model.Album;
import com.bragi.bragi.model.Artist;
import com.bragi.bragi.model.Song;
import com.bragi.bragi.repository.AlbumRepository;
import com.bragi.bragi.service.config.RetryConfig;
import org.hibernate.hql.internal.QueryExecutionRequestException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.text.html.Option;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    private AlbumService albumService;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private RetryConfig retryConfig;

    @BeforeEach
    void setUp() {
        when(retryConfig.getMaxAttempts()).thenReturn(3);
        when(retryConfig.getMinWaitBetweenMillis()).thenReturn(100L);
        when(retryConfig.getMaxWaitBetweenMillis()).thenReturn(200L);

        albumService = new AlbumService(albumRepository, retryConfig);

    }

    @AfterEach
    void tearDown() {
    }


    @Test
    void when_store_success_thenReturns() {
        var album = Album.builder()
                        .songs(new HashSet<>())
                                .artists(new HashSet<>())
                                        .title("hey").build();
        when(albumRepository.save(any())).thenReturn(album);

        var newAlbum = albumService.store(album);
        assertEquals(album, newAlbum);
        verify(albumRepository).save(any());
    }

    @Test
    void when_store_failsWithException_then_retry_thenSucceed(){
        var album = Album.builder()
                .songs(new HashSet<>())
                .artists(new HashSet<>())
                .title("hey").build();
        when(albumRepository.save(any()))
                .thenThrow(new QueryExecutionRequestException("da fuq?", "select * from blah"))
                .thenReturn(album);

        var newAlbum = albumService.store(album);
        assertEquals(album, newAlbum);
        verify(albumRepository, times(2)).save(any());

    }

    @Test
    void when_store_failsAfterRetryExhausted_thenFails(){
        var album = Album.builder()
                .songs(new HashSet<>())
                .artists(new HashSet<>())
                .title("hey").build();
        when(albumRepository.save(any()))
                .thenThrow(new QueryExecutionRequestException("da fuq?", "select * from blah"));
        assertThrows(RuntimeException.class, ()->{
            albumService.store(album);
        });
        verify(albumRepository, times(3)).save(any());

    }



    @Test
    void when_getById_success_thenReturns() {
        var album = Album.builder()
                .songs(new HashSet<>())
                .id(1L)
                .artists(new HashSet<>())
                .title("hey").build();
        when(albumRepository.findById(anyLong()))
                .thenReturn(Optional.of(album));

        var albumFromDb = albumService.getById(1L);
        assertEquals(album, albumFromDb);
        verify(albumRepository).findById(anyLong());
    }

    @Test
    void when_getById_failsThenRetries_thenReturns() {
        var album = Album.builder()
                .songs(new HashSet<>())
                .id(1L)
                .artists(new HashSet<>())
                .title("hey").build();
        when(albumRepository.findById(anyLong()))
                .thenThrow(new RuntimeException())
                .thenReturn(Optional.of(album));

        var albumFromDb = albumService.getById(1L);
        assertEquals(album, albumFromDb);
        verify(albumRepository, times(2)).findById(anyLong());
    }
    @Test
    void when_getById_failsThenRetries_exhausted_thenThrows() {
        var album = Album.builder()
                .songs(new HashSet<>())
                .id(1L)
                .artists(new HashSet<>())
                .title("hey").build();
        when(albumRepository.findById(anyLong()))
                .thenThrow(new RuntimeException());
        assertThrows(RuntimeException.class, ()->{
            albumService.getById(1L);
        });
        verify(albumRepository, times(3)).findById(anyLong());
    }

    @Test
    void when_deleteById_success_thenReturns() {
        assertDoesNotThrow(()->{
            albumService.deleteById(1L);
        });
        verify(albumRepository).deleteById(anyLong());

    }

    @Test
    void when_deleteById_retries_thenSuccess(){
        doThrow(new RuntimeException())
                .doNothing()
                .when(albumRepository)
                        .deleteById(anyLong());

        assertDoesNotThrow(()->{
            albumService.deleteById(1L);
        });

        verify(albumRepository, times(2)).deleteById(anyLong());
    }

    @Test
    void when_deleteById_retries_thenFails(){
        doThrow(new RuntimeException())
                .when(albumRepository)
                .deleteById(anyLong());

        assertThrows(RuntimeException.class, ()->{
            albumService.deleteById(1L);
        });

        verify(albumRepository, times(3)).deleteById(anyLong());
    }
    @Test
    void when_getAllArtists_success_thenReturns() {
        var artists = Set.of(Artist.builder()
                .name("jason").build(), Artist.builder().name("hello").build());

        var album = Album.builder()
                .songs(new HashSet<>())
                .id(1L)
                .artists(artists)
                .title("hey").build();

        when(albumRepository.findById(anyLong()))
                .thenReturn(Optional.of(album));

        var artistsFromDb = albumService.getAllArtists(1L);

        assertEquals(artists, artistsFromDb);

        verify(albumRepository).findById(1L);

    }

    @Test
    void when_getAllArtists_retries_thenReturns() {
        var artists = Set.of(Artist.builder()
                .name("jason").build(), Artist.builder().name("hello").build());

        var album = Album.builder()
                .songs(new HashSet<>())
                .id(1L)
                .artists(artists)
                .title("hey").build();

        when(albumRepository.findById(anyLong()))
                .thenThrow(new RuntimeException())
                .thenReturn(Optional.of(album));

        var artistsFromDb = albumService.getAllArtists(1L);

        assertEquals(artists, artistsFromDb);

        verify(albumRepository, times(2)).findById(1L);

    }

    @Test
    void when_getAllArtists_retries_thenThrows() {
        var artists = Set.of(Artist.builder()
                .name("jason").build(), Artist.builder().name("hello").build());

        var album = Album.builder()
                .songs(new HashSet<>())
                .id(1L)
                .artists(artists)
                .title("hey").build();

        when(albumRepository.findById(anyLong()))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, ()->{
            var artistsFromDb = albumService.getAllArtists(1L);

        });

        verify(albumRepository, times(3)).findById(1L);

    }

    @Test
    void when_getAllSongs_success_thenReturns() {
        var songs = Set.of(Song.builder()
                .title("jason").build(), Song.builder().title("hello").build());
        var album = Album.builder()
                .songs(songs)
                .id(1L)
                .artists(new HashSet<>())
                .title("hey").build();

        when(albumRepository.findById(anyLong()))
                .thenReturn(Optional.of(album));


        var songsFromDb = albumService.getAllSongs(1L);

        assertEquals(songs, songsFromDb);

        verify(albumRepository).findById(1L);

    }

    @Test
    void when_getAllSongs_retries_thenReturns() {
        var songs = Set.of(Song.builder()
                .title("jason").build(), Song.builder().title("hello").build());
        var album = Album.builder()
                .songs(songs)
                .id(1L)
                .artists(new HashSet<>())
                .title("hey").build();

        when(albumRepository.findById(anyLong()))
                .thenThrow(new RuntimeException())
                .thenReturn(Optional.of(album));


        var songsFromDb = albumService.getAllSongs(1L);

        assertEquals(songs, songsFromDb);

        verify(albumRepository, times(2)).findById(1L);

    }

    @Test
    void when_getAllSongs_retries_thenThrows() {
        var songs = Set.of(Song.builder()
                .title("jason").build(), Song.builder().title("hello").build());
        var album = Album.builder()
                .songs(songs)
                .id(1L)
                .artists(new HashSet<>())
                .title("hey").build();

        when(albumRepository.findById(anyLong()))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, ()->{
            var songsFromDb = albumService.getAllSongs(1L);

        });

        verify(albumRepository, times(3)).findById(1L);

    }

    @Test
    void when_getAlbumByName_success_thenReturns() {
        var album = Album.builder()
                .songs(new HashSet<>())
                .id(1L)
                .artists(new HashSet<>())
                .title("hey").build();
        when(albumRepository.findByTitle(anyString()))
                .thenReturn(Optional.of(album));

        var albumFromDb = albumService.getAlbumByName("hey");
        assertEquals(album, albumFromDb);
        verify(albumRepository).findByTitle(anyString());
    }

    @Test
    void when_getAlbumByName_retries_thenReturns() {
        var album = Album.builder()
                .songs(new HashSet<>())
                .id(1L)
                .artists(new HashSet<>())
                .title("hey").build();

        when(albumRepository.findByTitle(anyString()))
                .thenThrow(new RuntimeException())
                .thenReturn(Optional.of(album));

        var albumFromDb = albumService.getAlbumByName("hey");
        assertEquals(album, albumFromDb);
        verify(albumRepository, times(2)).findByTitle(anyString());
    }

    @Test
    void when_getAlbumByName_retries_thenThrows() {
        var album = Album.builder()
                .songs(new HashSet<>())
                .id(1L)
                .artists(new HashSet<>())
                .title("hey").build();

        when(albumRepository.findByTitle(anyString()))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, ()->{
            var albumFromDb = albumService.getAlbumByName("hey");
        });

        verify(albumRepository, times(3)).findByTitle(anyString());
    }
}