package com.bragi.bragi.service;

import com.bragi.bragi.model.Album;
import com.bragi.bragi.model.Artist;
import com.bragi.bragi.model.Song;
import com.bragi.bragi.repository.DataAccessService;
import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SongServiceTest {

    @Mock
    DataAccessService dataAccessService;

    SongService songService;

    @BeforeEach
    void setUp() {
        songService = new SongService(dataAccessService);
    }

    @Test
    void when_store_success_thenReturns() {
        when(dataAccessService.getArtistByExternalId(any()))
                .thenReturn(Artist.builder().build());
        when(dataAccessService.getAlbumByExternalId(any()))
                .thenReturn(Album.builder().build());

        var songId = UUID.randomUUID();
        when(dataAccessService.saveSong(any()))
                .thenReturn(Song.builder()
                        .id(1L)
                        .externalId(songId)
                        .build());

        var result = songService.store(brgi.grpc.AddSongRequest.newBuilder()
                .setSong(brgi.grpc.Song.newBuilder()
                        .setSongId(UUID.randomUUID().toString())
                        .setContent(ByteString.copyFrom(new byte[]{1, 2, 3}))
                        .setAlbumId(UUID.randomUUID().toString())
                        .addAllArtistId(List.of(UUID.randomUUID().toString()))
                        .setReleaseDate(Timestamp.newBuilder().build())
                        .build()).build());

        assertEquals(songId.toString(), result.getSongId());

    }

    @Test
    void when_store_saveSong_fails_thenThrows(){
        when(dataAccessService.getArtistByExternalId(any()))
                .thenReturn(Artist.builder().build());
        when(dataAccessService.getAlbumByExternalId(any()))
                .thenReturn(Album.builder().build());

        when(dataAccessService.saveSong(any()))
                .thenThrow(new RuntimeException("Cannot save"));

        assertThrows(RuntimeException.class, ()->{
            songService.store(brgi.grpc.AddSongRequest.newBuilder()
                    .setSong(brgi.grpc.Song.newBuilder()
                            .setSongId(UUID.randomUUID().toString())
                            .setContent(ByteString.copyFrom(new byte[]{1, 2, 3}))
                            .setAlbumId(UUID.randomUUID().toString())
                            .addAllArtistId(List.of(UUID.randomUUID().toString()))
                            .setReleaseDate(Timestamp.newBuilder().build())
                            .build()).build());
        });


    }

    @Test
    void when_store_getArtistByExternalId_fails_thenThrows(){
        when(dataAccessService.getArtistByExternalId(any()))
                .thenThrow(new RuntimeException("Cannot get artist"));


        assertThrows(RuntimeException.class, ()->{
            songService.store(brgi.grpc.AddSongRequest.newBuilder()
                    .setSong(brgi.grpc.Song.newBuilder()
                            .setSongId(UUID.randomUUID().toString())
                            .setContent(ByteString.copyFrom(new byte[]{1, 2, 3}))
                            .setAlbumId(UUID.randomUUID().toString())
                            .addAllArtistId(List.of(UUID.randomUUID().toString()))
                            .setReleaseDate(Timestamp.newBuilder().build())
                            .build()).build());
        });


    }

    @Test
    void when_store_getAlbumByExternalId_fails_thenThrows(){
        when(dataAccessService.getArtistByExternalId(any()))
                .thenReturn(Artist.builder().build());
        when(dataAccessService.getAlbumByExternalId(any()))
                .thenThrow(new RuntimeException("Cannot get album"));


        assertThrows(RuntimeException.class, ()->{
            songService.store(brgi.grpc.AddSongRequest.newBuilder()
                    .setSong(brgi.grpc.Song.newBuilder()
                            .setSongId(UUID.randomUUID().toString())
                            .setContent(ByteString.copyFrom(new byte[]{1, 2, 3}))
                            .setAlbumId(UUID.randomUUID().toString())
                            .addAllArtistId(List.of(UUID.randomUUID().toString()))
                            .setReleaseDate(Timestamp.newBuilder().build())
                            .build()).build());
        });


    }


    @Test
    void when_getSong_success_thenReturns() {
        when(dataAccessService.getSongByExternalId(any()))
                .thenReturn(Song.builder()
                        .id(1L)
                        .build());

       var song = songService.getSong(UUID.randomUUID());
       assertEquals(1L, song.getId());

    }

    @Test
    void when_getSong_getSongByExternalId_throws_thenThrows() {
        when(dataAccessService.getSongByExternalId(any()))
                .thenThrow(new RuntimeException("Cannot get song"));

        assertThrows(RuntimeException.class, ()->{
            var song = songService.getSong(UUID.randomUUID());
        });

    }

    @Test
    void when_getSongAlbum_success_thenReturns() {
        var albumId = UUID.randomUUID();
        when(dataAccessService.getSongByExternalId(any()))
                .thenReturn(Song.builder()
                        .album(Album
                                .builder()
                                .title("hey")
                                .date(java.sql.Timestamp.from(Instant.now()))
                                .externalId(albumId)
                                .build())
                        .build());

        var result = songService.getSongAlbum(brgi.grpc.GetSongAlbumRequest
                .newBuilder()
                        .setSongId(UUID.randomUUID().toString())
                .build());

        assertEquals(albumId.toString(), result.getAlbum().getAlbumId());
    }

    @Test
    void when_getSongAlbum_getSongByExternalId_throws_thenThrows() {
        var albumId = UUID.randomUUID();
        when(dataAccessService.getSongByExternalId(any()))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, ()->{
            var result = songService.getSongAlbum(brgi.grpc.GetSongAlbumRequest
                    .newBuilder()
                    .setSongId(UUID.randomUUID().toString())
                    .build());

        });

    }


    @Test
    void when_getSongsArtists_success_thenReturns() {
        var artistId = UUID.randomUUID();
        when(dataAccessService.getSongByExternalId(any()))
                .thenReturn(Song.builder()
                        .artists(Set.of(Artist.builder()
                                        .timeStarted(java.sql.Timestamp.from(Instant.now()))
                                        .name("hey")
                                        .externalId(artistId)
                                .build()))
                        .build());

        var result = songService.getSongsArtists(brgi.grpc.GetArtistsRequest.newBuilder()
                .setSongId(UUID.randomUUID().toString()).build());

        assertEquals(artistId.toString(), result.getArtist(0).getArtistId());


    }

    @Test
    void when_getSongsArtists_throws_thenThrows() {
        var artistId = UUID.randomUUID();
        when(dataAccessService.getSongByExternalId(any()))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, ()->{
            var result = songService.getSongsArtists(brgi.grpc.GetArtistsRequest.newBuilder()
                    .setSongId(UUID.randomUUID().toString()).build());
        });


    }

    @Test
    void when_deleteSong_success_thenReturns() {
        when(dataAccessService.getSongByExternalId(any()))
                .thenReturn(Song.builder().id(1L).build());

        assertDoesNotThrow(()->{
            songService.deleteSong(brgi.grpc.DeleteSongRequest.newBuilder().setSongId(UUID.randomUUID().toString())
                    .build());
        });
    }

    @Test
    void when_deleteSong_getSongByExternalId_throws_thenThrows() {
        when(dataAccessService.getSongByExternalId(any()))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, ()->{
            songService.deleteSong(brgi.grpc.DeleteSongRequest.newBuilder().setSongId(UUID.randomUUID().toString())
                    .build());
        });
    }

    @Test
    void when_deleteSong_deleteSongById_throws_thenThrows() {
        when(dataAccessService.getSongByExternalId(any()))
                .thenReturn(Song.builder().id(1L).build());
        doThrow(new RuntimeException())
                .when(dataAccessService).deleteSongById(anyLong());

        assertThrows(RuntimeException.class, ()->{
            songService.deleteSong(brgi.grpc.DeleteSongRequest.newBuilder().setSongId(UUID.randomUUID().toString())
                    .build());
        });
    }
}