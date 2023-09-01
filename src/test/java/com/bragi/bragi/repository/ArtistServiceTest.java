package com.bragi.bragi.repository;

import com.bragi.bragi.model.Album;
import com.bragi.bragi.model.Artist;
import com.bragi.bragi.model.Song;
import com.bragi.bragi.repository.AlbumRepository;
import com.bragi.bragi.repository.ArtistRepository;
import com.bragi.bragi.repository.DataAccessService;
import com.bragi.bragi.repository.SongRepository;
import com.bragi.bragi.service.config.RetryConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistServiceTest {

    private DataAccessService artistService;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private SongRepository songRepository;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private RetryConfig retryConfig;

    @BeforeEach
    void setUp() {
        when(retryConfig.getMaxAttempts()).thenReturn(3);
        when(retryConfig.getMinWaitBetweenMillis()).thenReturn(100L);
        when(retryConfig.getMaxWaitBetweenMillis()).thenReturn(200L);

        artistService = new DataAccessService(retryConfig, albumRepository, artistRepository, songRepository);
    }

    @Test
    void when_store_success_thenReturns() {
        var artist = Artist.builder()
                .songs(new HashSet<>())
                .albums(new HashSet<>())
                .build();

        when(artistRepository.save(any())).thenReturn(artist);

        var newArtist = artistService.saveArtist(artist);

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

        var newArtist = artistService.saveArtist(artist);

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
            var newArtist = artistService.saveArtist(artist);
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

        var newArtist = artistService.getArtistById(1L);

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

        var newArtist = artistService.getArtistById(1L);

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
            var newArtist = artistService.getArtistById(1L);

        });

        verify(artistRepository, times(3)).findById(anyLong());

    }

    @Test
    void when_deleteById_success_thenReturns() {
        assertDoesNotThrow(()->{
            artistService.deleteArtistById(1L);
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
            artistService.deleteArtistById(1L);
        });
        verify(artistRepository, times(2)).deleteById(anyLong());
    }

    @Test
    void when_deleteById_retries_thenThrows() {
        doThrow(new RuntimeException())
                .when(artistRepository)
                .deleteById(anyLong());

        assertThrows(RuntimeException.class, ()->{
            artistService.deleteArtistById(1L);
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

        var albums1 = artistService.getAllAlbumsFromArtistId(1L);

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

        var albums1 = artistService.getAllAlbumsFromArtistId(1L);

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
            var albums1 = artistService.getAllAlbumsFromArtistId(1L);
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

        var songs1 = artistService.getAllSongsFromArtistId(1L);

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

        var songs1 = artistService.getAllSongsFromArtistId(1L);

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
            var songs1 = artistService.getAllSongsFromArtistId(1L);

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

    @Test
    void when_getByExternalId_success_thenReturns() {
        var artist = Artist.builder()
                .name("hello")
                .songs(new HashSet<>())
                .albums(new HashSet<>())
                .build();

        when(artistRepository.findByExternalId(any())).thenReturn(Optional.of(artist));

        var artist1 = artistService.getArtistByExternalId(UUID.randomUUID());

        assertEquals(artist, artist1);

        verify(artistRepository).findByExternalId(any());

    }

    @Test
    void when_getExternalId_retries_thenReturns() {
        var artist = Artist.builder()
                .name("hello")
                .songs(new HashSet<>())
                .albums(new HashSet<>())
                .build();

        when(artistRepository.findByExternalId(any()))
                .thenThrow(new RuntimeException())
                .thenReturn(Optional.of(artist));

        var artist1 = artistService.getArtistByExternalId(UUID.randomUUID());

        assertEquals(artist, artist1);

        verify(artistRepository, times(2)).findByExternalId(any());

    }

    @Test
    void when_getByExternalId_retries_thenThrows() {
        var artist = Artist.builder()
                .name("hello")
                .songs(new HashSet<>())
                .albums(new HashSet<>())
                .build();

        when(artistRepository.findByExternalId(any()))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, ()->{
            var artist1 = artistService.getArtistByExternalId(UUID.randomUUID());
        });


        verify(artistRepository, times(3)).findByExternalId(any());

    }

    @Test
    void when_findAllArtists_success_thenReturns(){
        when(artistRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        var page = artistService.findAllArtists(PageRequest.of(1, 2));

        assertTrue(page.isEmpty());

        verify(artistRepository).findAll(any(Pageable.class));
    }

    @Test
    void when_findAllArtist_fails_thenThrows(){
        when(artistRepository.findAll(any(Pageable.class))).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, ()->{
            var page = artistService.findAllArtists(PageRequest.of(1, 2));
        });


        verify(artistRepository, times(3)).findAll(any(Pageable.class));

    }

}