package com.bragi.bragi.service;

import com.bragi.bragi.model.Album;
import com.bragi.bragi.model.Artist;
import com.bragi.bragi.model.Song;
import com.bragi.bragi.model.SongContent;
import com.bragi.bragi.repository.DataAccessService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArtistServiceTest {

    @Mock
    private DataAccessService dataAccessService;

    private ArtistService artistService;


    @BeforeEach
    void setUp() {
        artistService = new ArtistService(dataAccessService);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void when_store_success_thenReturns() {
        var externalId = UUID.randomUUID();
        when(dataAccessService.saveArtist(any()))
                .thenReturn(Artist.builder().externalId(externalId).build());

        var artistResponse = artistService.store(brgi.grpc.AddArtistRequest.newBuilder().build());

        assertEquals(externalId.toString(), artistResponse.getArtistId());
    }

    @Test
    void when_store_fails_thenThrows(){
        var externalId = UUID.randomUUID();
        when(dataAccessService.saveArtist(any()))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, ()->{
            var artistResponse = artistService
                    .store(brgi.grpc.AddArtistRequest.newBuilder().build());
        });
    }

    @Test
    void when_getArtist_success_thenReturns() {
        var externalId = UUID.randomUUID();
        when(dataAccessService.getArtistByExternalId(any()))
                .thenReturn(Artist.builder()
                        .id(1L)
                        .name("hey world")
                        .timeStarted(Timestamp.from(Instant.now()))
                        .externalId(externalId)
                        .songs(Set.of(Song.builder()
                                .externalId(UUID.randomUUID()).build()))
                        .build());

        var getArtistResponse = artistService.getArtist(brgi.grpc.GetArtistRequest.newBuilder()
                .setArtistId(externalId.toString()).build());

        assertEquals(externalId.toString(), getArtistResponse.getArtist().getArtistId());
        assertEquals(1, getArtistResponse.getArtist().getSongIdList().size());
    }

    @Test
    void when_getArtist_fails_thenThrows() {
        var externalId = UUID.randomUUID();
        when(dataAccessService.getArtistByExternalId(any()))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, ()->{
            var getArtistResponse = artistService.getArtist(brgi.grpc.GetArtistRequest.newBuilder()
                    .setArtistId(externalId.toString()).build());
        });
    }

    @Test
    void when_getSongs_success_thenReturns() {
        when(dataAccessService.getArtistByExternalId(any()))
                .thenReturn(Artist.builder()
                        .id(1L)
                        .name("hey world")
                        .timeStarted(Timestamp.from(Instant.now()))
                        .songs(Set.of(Song.builder()
                                .externalId(UUID.randomUUID()).build()))
                        .build());

        var songs = artistService.getSongs(UUID.randomUUID());

        assertEquals(1, songs.size());
    }

    @Test
    void when_getSongs_fails_thenThrows() {
        when(dataAccessService.getArtistByExternalId(any()))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, ()->{
            artistService.getSongs(UUID.randomUUID());
        });
    }

    @Test
    void when_getAllAlbums_success_thenReturns() {
        when(dataAccessService.getArtistByExternalId(any()))
                .thenReturn(Artist.builder()
                        .id(1L)
                        .name("hey world")
                        .timeStarted(Timestamp.from(Instant.now()))
                        .songs(Set.of(Song.builder()
                                .externalId(UUID.randomUUID()).build()))
                        .albums(Set.of(Album.builder()
                                        .date(Timestamp.from(Instant.now()))
                                        .title("title")
                                .externalId(UUID.randomUUID())
                                .build()))
                        .build());

        var response = artistService.getAllAlbums(brgi.grpc.GetAllAlbumsRequest.newBuilder()
                .setArtistId(UUID.randomUUID().toString()).build());

        assertEquals(1, response.getAlbumList().size());
    }

    @Test
    void when_getAllAlbums_fails_thenThrows() {
        when(dataAccessService.getArtistByExternalId(any()))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, ()->{
            var response = artistService.getAllAlbums(brgi.grpc.GetAllAlbumsRequest.newBuilder()
                    .setArtistId(UUID.randomUUID().toString()).build());
        });
    }

    @Test
    void when_getAllSongs_success_thenReturns() {
        when(dataAccessService.getArtistByExternalId(any()))
                .thenReturn(Artist.builder()
                        .id(1L)
                        .name("hey world")
                        .timeStarted(Timestamp.from(Instant.now()))
                        .songs(Set.of(Song.builder()
                                        .album(Album.builder()
                                                .externalId(UUID.randomUUID()).build())
                                        .title("hey world")
                                        .dateReleased(Timestamp.from(Instant.now()))
                                        .songContent(SongContent.builder()
                                                .content(new byte[]{1, 2, 3})
                                                .build())
                                .externalId(UUID.randomUUID()).build()))
                        .albums(Set.of(Album.builder()
                                .date(Timestamp.from(Instant.now()))
                                .title("title")
                                .externalId(UUID.randomUUID())
                                .build()))
                        .build());

        var response = artistService.getAllSongs(brgi.grpc.GetAllSongsRequest.newBuilder()
                .setArtistId(UUID.randomUUID().toString()).build());

        assertEquals(1, response.getSongList().size());
    }

    @Test
    void when_getAllSongs_fails_thenThrows() {
        when(dataAccessService.getArtistByExternalId(any()))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, ()->{
            var response = artistService.getAllSongs(brgi.grpc.GetAllSongsRequest.newBuilder()
                    .setArtistId(UUID.randomUUID().toString()).build());
        });

    }

    @Test
    void when_getAllSongs_failsWith_noArtistId_thenThrows(){
        assertThrows(RuntimeException.class, ()->{
            var response = artistService.getAllSongs(brgi.grpc.GetAllSongsRequest.newBuilder()
                    .build());
        });
    }

    @Test
    void when_deleteArtist_success_thenReturns() {
        when(dataAccessService.getArtistByExternalId(any())).thenReturn(Artist.builder()
                .id(1L).build());
        assertDoesNotThrow(()->{
            artistService.deleteArtist(brgi.grpc.DeleteArtistRequest.newBuilder()
                            .setArtistId(UUID.randomUUID().toString())
                    .build());
        });
    }

    @Test
    void when_deleteArtist_findArtistById_fails_thenThrows() {
        when(dataAccessService.getArtistByExternalId(any())).thenThrow(new NoSuchElementException());
        assertThrows(RuntimeException.class, ()->{
            artistService.deleteArtist(brgi.grpc.DeleteArtistRequest.newBuilder()
                    .setArtistId(UUID.randomUUID().toString())
                    .build());
        });
    }

    @Test
    void when_deleteArtist_failsToDelete_thenThrows() {
        when(dataAccessService.getArtistByExternalId(any())).thenReturn(Artist.builder()
                .id(1L).build());

        doThrow(new RuntimeException())
                .when(dataAccessService)
                .deleteArtistById(anyLong());

        assertThrows(RuntimeException.class, ()->{
            artistService.deleteArtist(brgi.grpc.DeleteArtistRequest.newBuilder()
                    .setArtistId(UUID.randomUUID().toString())
                    .build());
        });
    }
}