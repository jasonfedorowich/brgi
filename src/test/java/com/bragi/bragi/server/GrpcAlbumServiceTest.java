package com.bragi.bragi.server;

import com.bragi.bragi.model.Song;
import com.bragi.bragi.model.SongContent;
import com.bragi.bragi.service.AlbumService;
import io.grpc.internal.testing.StreamRecorder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GrpcAlbumServiceTest {

    private GrpcAlbumService grpcAlbumService;

    @Mock
    private AlbumService albumService;

    @BeforeEach
    void setUp() {
        grpcAlbumService = new GrpcAlbumService(albumService);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void when_addAlbum_success_thenReturns() throws Exception {
        var expected = brgi.grpc.AddAlbumResponse.newBuilder()
                .setAlbumId(UUID.randomUUID().toString()).build();
        when(albumService.store(any())).thenReturn(expected);

        StreamRecorder<brgi.grpc.AddAlbumResponse> responseObserver = StreamRecorder.create();

        grpcAlbumService.addAlbum(brgi.grpc.AddAlbumRequest.newBuilder().build(), responseObserver);

        responseObserver.awaitCompletion(1000, TimeUnit.MILLISECONDS);

        var actual = responseObserver.getValues().get(0);

        assertEquals(expected, actual);

    }

    @Test
    void when_addAlbum_fails_thenThrows() throws Exception {
        var expected = brgi.grpc.AddAlbumResponse.newBuilder()
                .setAlbumId(UUID.randomUUID().toString()).build();
        when(albumService.store(any())).thenThrow(new RuntimeException());

        StreamRecorder<brgi.grpc.AddAlbumResponse> responseObserver = StreamRecorder.create();

        grpcAlbumService.addAlbum(brgi.grpc.AddAlbumRequest.newBuilder().build(), responseObserver);

        responseObserver.awaitCompletion(1000, TimeUnit.MILLISECONDS);

        var actual = responseObserver.getError();

        assertTrue(actual instanceof RuntimeException);

    }


    @Test
    void when_getAlbum_success_thenReturns() throws Exception {
        var expected = brgi.grpc.GetAlbumResponse.newBuilder().build();
        when(albumService.getAlbum(any())).thenReturn(expected);

        StreamRecorder<brgi.grpc.GetAlbumResponse> responseObserver = StreamRecorder.create();

        grpcAlbumService.getAlbum(brgi.grpc.GetAlbumRequest.newBuilder().build(), responseObserver);

        responseObserver.awaitCompletion(1000, TimeUnit.MILLISECONDS);

        var actual = responseObserver.getValues().get(0);

        assertEquals(expected, actual);
    }

    @Test
    void when_getAlbum_fails_thenThrows() throws Exception {
        var expected = brgi.grpc.GetAlbumResponse.newBuilder().build();
        when(albumService.getAlbum(any())).thenThrow(new RuntimeException());

        StreamRecorder<brgi.grpc.GetAlbumResponse> responseObserver = StreamRecorder.create();

        grpcAlbumService.getAlbum(brgi.grpc.GetAlbumRequest.newBuilder().build(), responseObserver);

        responseObserver.awaitCompletion(1000, TimeUnit.MILLISECONDS);

        var actual = responseObserver.getError();

        assertTrue(actual instanceof RuntimeException);
    }

    @Test
    void when_streamAlbum_success_thenReturns() throws Exception {
        when(albumService.getSongs(any()))
                .thenReturn(Set.of(Song.builder()
                        .songContent(SongContent.builder()
                                .content(new byte[]{1, 2, 3})
                                .build()).build()));

        StreamRecorder<brgi.grpc.StreamAlbumResponse> responseObserver = StreamRecorder.create();

        grpcAlbumService.streamAlbum(brgi.grpc.StreamAlbumRequest.newBuilder()
                        .setAlbumId(UUID.randomUUID().toString())
                .build(), responseObserver);

        responseObserver.awaitCompletion(1000, TimeUnit.MILLISECONDS);
        var actual = responseObserver.getValues();
        assertEquals((byte)1, actual.get(0).getContent().toByteArray()[0]);

    }

    @Test
    void when_streamAlbum_fails_thenThrows() throws Exception {
        when(albumService.getSongs(any()))
                .thenThrow(new RuntimeException());

        StreamRecorder<brgi.grpc.StreamAlbumResponse> responseObserver = StreamRecorder.create();

        grpcAlbumService.streamAlbum(brgi.grpc.StreamAlbumRequest.newBuilder()
                .setAlbumId(UUID.randomUUID().toString())
                .build(), responseObserver);

        responseObserver.awaitCompletion(1000, TimeUnit.MILLISECONDS);
        var actual = responseObserver.getError();

        assertTrue(actual instanceof RuntimeException);

    }


    @Test
    void when_getAllSongs_success_thenReturns() throws Exception {
        when(albumService.getAllSongs(any())).thenReturn(brgi.grpc.GetAllSongsResponse.newBuilder().build());

        StreamRecorder<brgi.grpc.GetAllSongsResponse> responseObserver = StreamRecorder.create();

        grpcAlbumService.getAllSongs(brgi.grpc.GetAllSongsRequest.getDefaultInstance(), responseObserver);

        responseObserver.awaitCompletion(1000, TimeUnit.MILLISECONDS);
        var actual = responseObserver.getValues();
        assertEquals(brgi.grpc.GetAllSongsResponse.getDefaultInstance(), actual.get(0));
    }

    @Test
    void when_getAllSongs_fails_thenThrows() throws Exception {
        when(albumService.getAllSongs(any())).thenThrow(new RuntimeException());

        StreamRecorder<brgi.grpc.GetAllSongsResponse> responseObserver = StreamRecorder.create();

        grpcAlbumService.getAllSongs(brgi.grpc.GetAllSongsRequest.getDefaultInstance(), responseObserver);

        responseObserver.awaitCompletion(1000, TimeUnit.MILLISECONDS);
        var actual = responseObserver.getError();
        assertTrue(actual instanceof RuntimeException);
    }

    @Test
    void when_getAlbumArtists_success_thenReturns() throws Exception {
        when(albumService.getArtists(any())).thenReturn(brgi.grpc.GetArtistsResponse.getDefaultInstance());

        StreamRecorder<brgi.grpc.GetArtistsResponse> responseObserver = StreamRecorder.create();

        grpcAlbumService.getAlbumArtists(brgi.grpc.GetArtistsRequest.getDefaultInstance(), responseObserver);

        responseObserver.awaitCompletion(1000, TimeUnit.MILLISECONDS);
        var actual = responseObserver.getValues();
        assertEquals(brgi.grpc.GetArtistsResponse.getDefaultInstance(), actual.get(0));
    }

    @Test
    void when_getAlbumArtists_fails_thenThrows() throws Exception {
        when(albumService.getArtists(any())).thenThrow(new RuntimeException());

        StreamRecorder<brgi.grpc.GetArtistsResponse> responseObserver = StreamRecorder.create();

        grpcAlbumService.getAlbumArtists(brgi.grpc.GetArtistsRequest.getDefaultInstance(), responseObserver);

        responseObserver.awaitCompletion(1000, TimeUnit.MILLISECONDS);
        var actual = responseObserver.getError();
        assertTrue(actual instanceof RuntimeException);
    }

    @Test
    void when_deleteAlbum_success_thenReturns() throws Exception {
        when(albumService.deleteById(any())).thenReturn(brgi.grpc.DeleteAlbumResponse.getDefaultInstance());

        StreamRecorder<brgi.grpc.DeleteAlbumResponse> responseObserver = StreamRecorder.create();

        grpcAlbumService.deleteAlbum(brgi.grpc.DeleteAlbumRequest.getDefaultInstance(), responseObserver);

        responseObserver.awaitCompletion(1000, TimeUnit.MILLISECONDS);
        var actual = responseObserver.getValues();
        assertEquals(brgi.grpc.DeleteAlbumResponse.getDefaultInstance(), actual.get(0));
    }

    @Test
    void when_deleteAlbum_fails_thenThrows() throws Exception {
        when(albumService.deleteById(any())).thenThrow(new RuntimeException());

        StreamRecorder<brgi.grpc.DeleteAlbumResponse> responseObserver = StreamRecorder.create();

        grpcAlbumService.deleteAlbum(brgi.grpc.DeleteAlbumRequest.getDefaultInstance(), responseObserver);

        responseObserver.awaitCompletion(1000, TimeUnit.MILLISECONDS);
        var actual = responseObserver.getError();
        assertTrue(actual instanceof RuntimeException);
    }
}