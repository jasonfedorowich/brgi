package com.bragi.bragi.service;

import com.bragi.bragi.model.Album;
import com.bragi.bragi.model.Artist;
import com.bragi.bragi.model.Song;
import com.bragi.bragi.model.SongContent;
import com.bragi.bragi.repository.DataAccessService;
import org.checkerframework.checker.units.qual.A;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    @Mock
    DataAccessService dataAccessService;

    private AlbumService albumService;

    @BeforeEach
    void setUp(){
        albumService = new AlbumService(dataAccessService);
    }

    @Test
    void when_store_success_thenReturns() {
        var id = UUID.randomUUID();

        when(dataAccessService.getArtistByExternalId(any())).thenReturn(Artist
                .builder()
                        .id(1L)
                        .externalId(UUID.randomUUID())
                .build());

        when(dataAccessService.saveAlbum(any())).thenReturn(Album.builder()
                .id(1L)
                .externalId(id).build());

        brgi.grpc.AddAlbumResponse response = albumService.store(brgi.grpc.AddAlbumRequest
                .newBuilder()
                        .setAlbum(brgi.grpc.Album.newBuilder()
                                .addAllArtistId(List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString())))
                .build());

        assertEquals(id, UUID.fromString(response.getAlbumId()));

    }

    @Test
    void when_store_fails_getArtistsByExternalId_thenThrows(){
        when(dataAccessService.getArtistByExternalId(any())).thenReturn(Artist
                .builder()
                .id(1L)
                .externalId(UUID.randomUUID())
                .build());


        assertThrows(RuntimeException.class, ()->{
            brgi.grpc.AddAlbumResponse response = albumService.store(brgi.grpc.AddAlbumRequest
                    .newBuilder()
                    .setAlbum(brgi.grpc.Album.newBuilder()
                            .addAllArtistId(List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString())))
                    .build());
        });

        verify(dataAccessService, times(2)).getArtistByExternalId(any());
    }

    @Test
    void when_store_fails_getArtistByExternalId_thenThrows(){
        when(dataAccessService.getArtistByExternalId(any())).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, ()->{
            brgi.grpc.AddAlbumResponse response = albumService.store(brgi.grpc.AddAlbumRequest
                    .newBuilder()
                    .setAlbum(brgi.grpc.Album.newBuilder()
                            .addAllArtistId(List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString())))
                    .build());
        });

        verify(dataAccessService, times(0)).saveArtist(any());
    }
    @Test
    void when_getSongs_success_thenReturns() {
        when(dataAccessService.getAlbumByExternalId(any())).thenReturn(Album
                .builder()
                .id(1L)
                .externalId(UUID.randomUUID())
                        .songs(Set.of(Song.builder().build()))
                .build());

        var songs = albumService.getSongs(UUID.randomUUID());
        assertEquals(1, songs.size());

    }

    @Test
    void when_getSongs_fails_thenThrows() {
        when(dataAccessService.getAlbumByExternalId(any())).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, ()->{
            var songs = albumService.getSongs(UUID.randomUUID());
        });
    }

    @Test
    void when_deleteById_success_thenReturns() {
        when(dataAccessService.getAlbumByExternalId(any())).thenReturn(Album
                .builder()
                .id(1L)
                .externalId(UUID.randomUUID())
                .songs(Set.of(Song.builder().build()))
                .build());

        assertDoesNotThrow(()->{
            albumService.deleteById(brgi.grpc.DeleteAlbumRequest.newBuilder()
                    .setAlbumId(UUID.randomUUID().toString()).build());
        });
    }

    @Test
    void when_deleteById_getAlbumByExternalId_fails_thenThrows() {
        when(dataAccessService.getAlbumByExternalId(any())).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, ()->{
            albumService.deleteById(brgi.grpc.DeleteAlbumRequest.newBuilder()
                    .setAlbumId(UUID.randomUUID().toString()).build());
        });
    }

    @Test
    void when_getAlbum_success_thenReturns() {
        when(dataAccessService.getAlbumByExternalId(any())).thenReturn(Album
                .builder()
                .id(1L)
                        .title("hey world")
                        .date(Timestamp.from(Instant.now()))
                .externalId(UUID.randomUUID())
                .songs(Set.of(Song.builder()
                        .externalId(UUID.randomUUID()).build()))
                        .artists(Set.of(Artist.builder()
                                .externalId(UUID.randomUUID())
                                .build()))
                .build());

        var response = albumService.getAlbum(brgi.grpc.GetAlbumRequest.newBuilder()
                .setAlbumId(UUID.randomUUID().toString()).build());

        assertEquals(1, response.getAlbum().getArtistIdList().size());
        assertEquals(1, response.getAlbum().getSongIdList().size());
    }

    @Test
    void when_getAlbum_getAlbumByExternalId_fails_thenThrows() {
        when(dataAccessService.getAlbumByExternalId(any())).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, ()->{
            var response = albumService.getAlbum(brgi.grpc.GetAlbumRequest.newBuilder()
                    .setAlbumId(UUID.randomUUID().toString()).build());
        });

    }


    @Test
    void when_getArtists_success_thenReturns(){
        when(dataAccessService.getAlbumByExternalId(any())).thenReturn(Album
                .builder()
                .id(1L)
                .title("hey world")
                .date(Timestamp.from(Instant.now()))
                .externalId(UUID.randomUUID())
                .songs(Set.of(Song.builder()
                        .externalId(UUID.randomUUID()).build()))
                .artists(Set.of(Artist.builder()
                        .externalId(UUID.randomUUID())
                                .timeStarted(Timestamp.from(Instant.now()))
                                .name("hej")
                        .build()))
                .build());

        var response = albumService.getArtists(brgi.grpc.GetArtistsRequest.newBuilder()
                .setAlbumId(UUID.randomUUID().toString()).build());

        assertEquals(1, response.getArtistList().size());

    }

    @Test
    void when_getArtists_failsAtGetAlbumExternalId_thenThrows(){
        when(dataAccessService.getAlbumByExternalId(any()))
                .thenThrow(new RuntimeException("Cant access db"));

        assertThrows(RuntimeException.class, ()->{
            var response = albumService.getArtists(brgi.grpc.GetArtistsRequest.newBuilder()
                    .setAlbumId(UUID.randomUUID().toString()).build());
        });

    }

    @Test
    void when_getArtists_requestDoesNotHaveAlbumId_thenThrows(){

        assertThrows(RuntimeException.class, ()->{
            var response = albumService.getArtists(brgi.grpc.GetArtistsRequest.getDefaultInstance());
        });

    }

    @Test
    void when_getAllSongs_success_thenReturns(){
        when(dataAccessService.getAlbumByExternalId(any())).thenReturn(Album
                .builder()
                .id(1L)
                .title("hey world")
                .date(Timestamp.from(Instant.now()))
                .externalId(UUID.randomUUID())
                .songs(Set.of(Song.builder()
                        .externalId(UUID.randomUUID())
                                .songContent(SongContent.builder()
                                        .content(new byte[]{1, 2, 3})
                                        .build())
                                .album(Album.builder().externalId(UUID.randomUUID()).build())
                                .title("hey")
                                .dateReleased(Timestamp.from(Instant.now()))
                                .externalId(UUID.randomUUID())
                        .build()))
                .artists(Set.of(Artist.builder()
                        .externalId(UUID.randomUUID())
                        .timeStarted(Timestamp.from(Instant.now()))
                        .name("hej")
                        .build()))
                .build());

        var response = albumService.getAllSongs(brgi.grpc.GetAllSongsRequest.newBuilder()
                        .setAlbumId(UUID.randomUUID().toString())
                .build());

        assertEquals(1, response.getSongList().size());
    }

    @Test
    void when_getAllSongs_dataAccessFails_thenThrows(){
        when(dataAccessService.getAlbumByExternalId(any()))
                .thenThrow(new RuntimeException("Cant access data source"));

        assertThrows(RuntimeException.class, ()->{
            var response = albumService.getAllSongs(brgi.grpc.GetAllSongsRequest.newBuilder()
                    .setAlbumId(UUID.randomUUID().toString())
                    .build());
        });

    }

    @Test
    void when_getAllSongs_requestDoesNotHaveAlbumId_thenThrows(){

        assertThrows(RuntimeException.class, ()->{
            var response = albumService.getAllSongs(brgi.grpc.GetAllSongsRequest.newBuilder()
                    .build());
        });

    }

    @Test
    void when_findAllAlbums_success_thenReturns(){
        when(dataAccessService.findAllAlbums(any()))
                .thenReturn(Page.empty());
        List<Album> albumPage = albumService.findAllAlbums(1, 1, "desc");
        assertTrue(albumPage.isEmpty());

    }
    @Test
    void when_findAllAlbums_invalidSort_thenThrows(){
        assertThrows(RuntimeException.class, ()->{
            albumService.findAllAlbums(1, 1, "invalid");
        });
    }

    @Test
    void when_findAllArtists_dataAccessFails_thenThrows(){
        when(dataAccessService.findAllAlbums(any()))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, ()->{
            albumService.findAllAlbums(1, 1, "desc");
        });
    }

    @Test
    void when_getAlbumById_success_thenReturns(){
        var uuid = UUID.randomUUID();
        when(dataAccessService.getAlbumByExternalId(uuid))
                .thenReturn(Album.builder().build());

        var album = albumService.getAlbum(uuid.toString());
        assertEquals(Album.builder().build(), album);
    }

    @Test
    void when_getAlbumById_fails_thenThrows(){
        var uuid = UUID.randomUUID();
        when(dataAccessService.getAlbumByExternalId(uuid))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, ()->{
            var album = albumService.getAlbum(uuid.toString());
        });

    }
    @Test
    void when_getAlbumById_invalidId_thenThrows(){
        assertThrows(RuntimeException.class, ()->{
            var artist = albumService.getAlbum("invalid-uuid");
        });
    }

    @Test
    void when_storeDto_success_thenReturns(){
        when(dataAccessService.getArtistByExternalId(any()))
                .thenReturn(Artist.builder().build());
        when(dataAccessService.saveAlbum(any()))
                .thenReturn(Album.builder().build());

        var album = albumService.store(com.bragi.bragi.rest.dto.Album.builder()
                .artists(Set.of(UUID.randomUUID())).build());

        assertEquals(Album.builder().build(), album);
    }

    @Test
    void when_storeDto_failsLookup_thenThrows(){
        when(dataAccessService.getArtistByExternalId(any()))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, ()->{
            var album = albumService.store(com.bragi.bragi.rest.dto.Album.builder()
                    .artists(Set.of(UUID.randomUUID())).build());
        });

    }

    @Test
    void when_storeDto_failsToStore_thenThrows(){
        when(dataAccessService.getArtistByExternalId(any()))
                .thenReturn(Artist.builder().build());
        when(dataAccessService.saveAlbum(any()))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, ()->{
            var album = albumService.store(com.bragi.bragi.rest.dto.Album.builder()
                    .artists(Set.of(UUID.randomUUID())).build());
        });

    }

    @Test
    void when_deleteAlbumId_success_thenReturns(){
        when(dataAccessService.getAlbumByExternalId(any())).thenReturn(Album.builder().build());
        assertDoesNotThrow(()->{
            albumService.deleteAlbum(UUID.randomUUID().toString());
        });

    }

    @Test
    void when_deleteAlbumId_fails_noArtist_thenThrows(){
        when(dataAccessService.getAlbumByExternalId(any())).thenThrow(new NoSuchElementException());
        assertThrows(RuntimeException.class, ()->{
            albumService.deleteAlbum(UUID.randomUUID().toString());
        });
    }

    @Test
    void when_deleteAlbumId_fails_failsToDelete_thenThrows(){
        when(dataAccessService.getAlbumByExternalId(any())).thenReturn(Album.builder().build());
        doThrow(new RuntimeException())
                .when(dataAccessService)
                .deleteAlbumById(anyLong());
        assertThrows(RuntimeException.class, ()->{
            albumService.deleteAlbum(UUID.randomUUID().toString());
        });
    }



}