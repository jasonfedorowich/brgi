package com.bragi.bragi.rest;

import com.bragi.bragi.metrics.ServiceMetrics;
import com.bragi.bragi.model.Artist;
import com.bragi.bragi.service.ArtistService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistControllerTest {

    @Mock
    private ServiceMetrics serviceMetrics;

    @Mock
    private ArtistService artistService;

    private ArtistController artistController;

    @BeforeEach
    void setUp() {
        artistController = new ArtistController(artistService, serviceMetrics);
    }


    @Test
    void when_getArtists_success_thenReturns() {
        List<Artist> artists = List.of(Artist
                .builder()
                .name("hel")
                .externalId(UUID.randomUUID())
                .timeStarted(Timestamp.from(Instant.now()))
                .songs(Set.of())
                .albums(Set.of())
                .build());
        when(artistService.findAllArtists(anyInt(), anyInt(), anyString())).thenReturn(artists);

        StepVerifier.create(artistController.getArtists(1, 1, "desc"))
                .consumeNextWith(next->{
                    assertEquals(1, next.size());
                }).verifyComplete();

        verify(serviceMetrics).recordRestLatency(eq("get_artists"), anyLong());
    }

    @Test
    void when_getArtist_success_thenReturns() {
        Artist artist = Artist
                .builder()
                .name("hel")
                .externalId(UUID.randomUUID())
                .timeStarted(Timestamp.from(Instant.now()))
                .songs(Set.of())
                .albums(Set.of())
                .build();
        when(artistService.getArtist(anyString()))
                .thenReturn(artist);

        StepVerifier.create(artistController.getArtist(UUID.randomUUID().toString()))
                .consumeNextWith(next->{
                    assertEquals(com.bragi.bragi.rest.dto.Artist.convertToDto(artist), next);
                })
                .verifyComplete();

        verify(serviceMetrics).recordRestLatency(eq("get_artist"), anyLong());

    }

    @Test
    void when_createArtist_success_thenReturns() {
        Artist artist = Artist
                .builder()
                .name("hel")
                .externalId(UUID.randomUUID())
                .timeStarted(Timestamp.from(Instant.now()))
                .songs(Set.of())
                .albums(Set.of())
                .build();

        when(artistService.store(any(com.bragi.bragi.rest.dto.Artist.class)))
                .thenReturn(artist);

        StepVerifier.create(artistController.createArtist(com.bragi.bragi.rest.dto.Artist.builder().build()))
                .consumeNextWith(next->{
                    assertEquals(com.bragi.bragi.rest.dto.Artist.convertToDto(artist), next);
                }).verifyComplete();

        verify(serviceMetrics).recordRestLatency(eq("create_artist"), anyLong());


    }

    @Test
    void when_deleteArtist_success_thenReturns() {
        Artist artist = Artist
                .builder()
                .name("hel")
                .externalId(UUID.randomUUID())
                .timeStarted(Timestamp.from(Instant.now()))
                .songs(Set.of())
                .albums(Set.of())
                .build();


        StepVerifier.create(artistController.deleteArtist(UUID.randomUUID().toString()))
                .expectNext()
                .verifyComplete();

        verify(serviceMetrics).recordRestLatency(eq("delete_artist"), anyLong());
    }
}