package com.bragi.bragi.rest;

import com.bragi.bragi.metrics.ServiceMetrics;
import com.bragi.bragi.model.Artist;
import com.bragi.bragi.rest.dto.Album;
import com.bragi.bragi.service.AlbumService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlbumControllerTest {

    @Mock
    private ServiceMetrics serviceMetrics;

    @Mock
    private AlbumService albumService;

    private AlbumController albumController;

    @BeforeEach
    public void setUp(){
        albumController = new AlbumController(albumService, serviceMetrics);
    }

    @Test
    void when_getAlbums_success_thenReturns() {
        List<com.bragi.bragi.model.Album> albums = List.of(com.bragi.bragi.model.Album
                .builder()
                .title("hel")
                .externalId(UUID.randomUUID())
                .date(Timestamp.from(Instant.now()))
                .songs(Set.of())
                .artists(Set.of())
                .build());
        when(albumService.findAllAlbums(anyInt(), anyInt(), anyString())).thenReturn(albums);

        StepVerifier.create(albumController.getAlbums(1, 1, "desc"))
                .consumeNextWith(next->{
                    assertEquals(1, next.size());
                }).verifyComplete();

        verify(serviceMetrics).recordRestLatency(eq("get_albums"), anyLong());
    }

    @Test
    void when_getAlbum_success_thenReturns() {
        com.bragi.bragi.model.Album album = com.bragi.bragi.model.Album
                .builder()
                .title("hel")
                .externalId(UUID.randomUUID())
                .date(Timestamp.from(Instant.now()))
                .songs(Set.of())
                .artists(Set.of())
                .build();

        when(albumService.getAlbum(anyString()))
                .thenReturn(album);

        StepVerifier.create(albumController.getAlbum(UUID.randomUUID().toString()))
                .consumeNextWith(next->{
                    assertEquals(com.bragi.bragi.rest.dto.Album.convertToDto(album), next);
                })
                .verifyComplete();

        verify(serviceMetrics).recordRestLatency(eq("get_album"), anyLong());

    }

    @Test
    void when_createAlbum_success_thenReturns() {
        com.bragi.bragi.model.Album album = com.bragi.bragi.model.Album
                .builder()
                .title("hel")
                .externalId(UUID.randomUUID())
                .date(Timestamp.from(Instant.now()))
                .songs(Set.of())
                .artists(Set.of())
                .build();

        when(albumService.store(any(com.bragi.bragi.rest.dto.Album.class)))
                .thenReturn(album);

        StepVerifier.create(albumController.createAlbum(com.bragi.bragi.rest.dto.Album.builder().build()))
                .consumeNextWith(next->{
                    assertEquals(com.bragi.bragi.rest.dto.Album.convertToDto(album), next);
                }).verifyComplete();

        verify(serviceMetrics).recordRestLatency(eq("create_album"), anyLong());

    }

    @Test
    void when_deleteArtist_success_thenReturns() {
        com.bragi.bragi.model.Album album = com.bragi.bragi.model.Album
                .builder()
                .title("hel")
                .externalId(UUID.randomUUID())
                .date(Timestamp.from(Instant.now()))
                .songs(Set.of())
                .artists(Set.of())
                .build();


        StepVerifier.create(albumController.deleteAlbum(UUID.randomUUID().toString()))
                .expectNext()
                .verifyComplete();

        verify(serviceMetrics).recordRestLatency(eq("delete_album"), anyLong());

    }
}