package com.bragi.bragi.service;

import com.bragi.bragi.model.Album;
import com.bragi.bragi.model.Artist;
import com.bragi.bragi.model.Song;
import com.bragi.bragi.model.SongContent;
import com.bragi.bragi.repository.DataAccessService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

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

    @Test
    void when_findAllArtists_success_thenReturns(){
        when(dataAccessService.findAllArtists(any()))
                .thenReturn(Page.empty());
        List<Artist> artistPage = artistService.findAllArtists(1, 1, "desc");
        assertTrue(artistPage.isEmpty());

    }
    @Test
    void when_findAllArtists_invalidSort_thenThrows(){
        assertThrows(RuntimeException.class, ()->{
            artistService.findAllArtists(1, 1, "invalid");
        });
    }

    @Test
    void when_findAllArtists_dataAccessFails_thenThrows(){
        when(dataAccessService.findAllArtists(any()))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, ()->{
            artistService.findAllArtists(1, 1, "desc");
        });
    }

    @Test
    void when_getArtistById_success_thenReturns(){
        var uuid = UUID.randomUUID();
        when(dataAccessService.getArtistByExternalId(uuid))
                .thenReturn(Artist.builder().build());

        var artist = artistService.getArtist(uuid.toString());
        assertEquals(Artist.builder().build(), artist);
    }

    @Test
    void when_getArtistById_fails_thenThrows(){
        var uuid = UUID.randomUUID();
        when(dataAccessService.getArtistByExternalId(uuid))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, ()->{
            var artist = artistService.getArtist(uuid.toString());
        });

    }

    @Test
    void when_getArtistById_invalidId_thenThrows(){
        assertThrows(RuntimeException.class, ()->{
            var artist = artistService.getArtist("invalid-uuid");
        });
    }

    @Test
    void when_storeDto_success_thenReturns(){
        var externalId = UUID.randomUUID();
        Artist expected = Artist.builder().externalId(externalId).build();
        when(dataAccessService.saveArtist(any()))
                .thenReturn(expected);

        var artist = artistService.store(com.bragi.bragi.rest.dto.Artist.builder()
                        .timeStarted(Timestamp.from(Instant.now()))
                        .name("hello world")
                .build());

        assertEquals(expected, artist);
    }

    @Test
    void when_storeDto_fails_thenThrows(){
        var externalId = UUID.randomUUID();
        Artist expected = Artist.builder().externalId(externalId).build();
        when(dataAccessService.saveArtist(any()))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, ()->{
            var artist = artistService.store(com.bragi.bragi.rest.dto.Artist.builder()
                    .timeStarted(Timestamp.from(Instant.now()))
                    .name("hello world")
                    .build());
        });

    }

    @Test
    void when_deleteArtistId_success_thenReturns(){
        when(dataAccessService.getArtistByExternalId(any())).thenReturn(Artist.builder().build());
        assertDoesNotThrow(()->{
            artistService.deleteArtist(UUID.randomUUID().toString());
        });

    }

    @Test
    void when_deleteArtistId_fails_noArtist_thenThrows(){
        when(dataAccessService.getArtistByExternalId(any())).thenThrow(new NoSuchElementException());
        assertThrows(RuntimeException.class, ()->{
            artistService.deleteArtist(UUID.randomUUID().toString());
        });
    }

    @Test
    void when_deleteArtistId_fails_failsToDelete_thenThrows(){
        when(dataAccessService.getArtistByExternalId(any())).thenReturn(Artist.builder().build());
        doThrow(new RuntimeException())
                .when(dataAccessService)
                .deleteArtistById(anyLong());
        assertThrows(RuntimeException.class, ()->{
            artistService.deleteArtist(UUID.randomUUID().toString());
        });
    }

}