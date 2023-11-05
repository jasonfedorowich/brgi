package com.bragi.bragi.rest;

import com.bragi.bragi.metrics.ServiceMetrics;
import com.bragi.bragi.model.Album;
import com.bragi.bragi.model.Artist;
import com.bragi.bragi.model.Song;
import com.bragi.bragi.model.SongContent;
import com.bragi.bragi.service.SongService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import reactor.test.StepVerifier;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SongControllerTest {

    @Mock
    ServiceMetrics serviceMetrics;

    @Mock
    SongService songService;

    SongController songController;

    @BeforeEach
    public void setUp(){
        songController = new SongController(songService, serviceMetrics);
    }



    @Test
    void when_getSongs_success_thenReturns() {
        var song = Song.builder()
                .album(Album.builder().externalId(UUID.randomUUID()).build())
                .artists(Set.of(Artist.builder().externalId(UUID.randomUUID()).build()))
                        .title("title")
                                .dateReleased(Timestamp.from(Instant.now()))
                                        .duration(100)
                .id(1L)
                .externalId(UUID.randomUUID())
                .build();

        when(songService.findAllSongs(anyInt(), anyInt(), anyString())).thenReturn(List.of(song));

        StepVerifier.create(songController.getSongs(1, 1, "desc"))
                .consumeNextWith((next)->{
                    assertEquals(1, next.size());
                }).verifyComplete();

        verify(serviceMetrics).recordRestLatency(eq("get_songs"), anyLong());

    }

    @Test
    void when_getSong_success_thenReturns() {
        var song = Song.builder()
                .album(Album.builder().externalId(UUID.randomUUID()).build())
                .artists(Set.of(Artist.builder().externalId(UUID.randomUUID()).build()))
                .title("title")
                .dateReleased(Timestamp.from(Instant.now()))
                .duration(100)
                .id(1L)
                .externalId(UUID.randomUUID())
                .build();

        when(songService.getSong(any(UUID.class))).thenReturn(song);

        StepVerifier.create(songController.getSong(UUID.randomUUID().toString()))
                .consumeNextWith((next)->{
                    assertEquals(song.getTitle(), next.getTitle());
                }).verifyComplete();

        verify(serviceMetrics).recordRestLatency(eq("get_song"), anyLong());
    }

    @Test
    void when_createSong_success_thenReturns() {
        var song = Song.builder()
                .album(Album.builder().externalId(UUID.randomUUID()).build())
                .artists(Set.of(Artist.builder().externalId(UUID.randomUUID()).build()))
                .title("title")
                .dateReleased(Timestamp.from(Instant.now()))
                .duration(100)
                .id(1L)
                .externalId(UUID.randomUUID())
                .build();

        when(songService.store(any(), any())).thenReturn(song);

        var songRequest = com.bragi.bragi.rest.dto.Song.convertToDto(song);
        var file = mock(MultipartFile.class);

        StepVerifier.create(songController.createSong(songRequest, file))
                .consumeNextWith(next->{
                   assertEquals(song.getTitle(), next.getTitle());
                }).verifyComplete();

        verify(serviceMetrics).recordRestLatency(eq("create_song"), anyLong());

    }

    @Test
    void when_downloadFile_success_thenReturns() {
        var song = Song.builder()
                .album(Album.builder().externalId(UUID.randomUUID()).build())
                .artists(Set.of(Artist.builder().externalId(UUID.randomUUID()).build()))
                .title("title")
                .songContent(SongContent.builder()
                        .content(new byte[]{1, 2, 3}).build())
                .dateReleased(Timestamp.from(Instant.now()))
                .duration(100)
                .id(1L)
                .externalId(UUID.randomUUID())
                .build();

        when(songService.getSong(any(UUID.class))).thenReturn(song);

        StepVerifier.create(songController.downloadFile(UUID.randomUUID().toString()))
                .consumeNextWith((next)->{
                    assertEquals(3, next.length);
                }).verifyComplete();

        verify(serviceMetrics).recordRestLatency(eq("download_song"), anyLong());
    }

    @Test
    void when_deleteSong_success_thenReturns() {

        StepVerifier.create(songController.deleteSong(UUID.randomUUID().toString()))
                        .expectNext()
                .verifyComplete();

        verify(serviceMetrics).recordRestLatency(eq("delete_song"), anyLong());
    }
}