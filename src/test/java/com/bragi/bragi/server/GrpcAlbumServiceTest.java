package com.bragi.bragi.server;

import com.bragi.bragi.metrics.ServiceMetrics;
import com.bragi.bragi.model.Song;
import com.bragi.bragi.model.SongContent;
import com.bragi.bragi.server.handlers.AlbumHandler;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GrpcAlbumServiceTest {

    private GrpcAlbumService grpcAlbumService;

    @Mock
    private AlbumHandler albumHandler;

    @Mock
    private ServiceMetrics serviceMetrics;

    @BeforeEach
    void setUp() {
        grpcAlbumService = new GrpcAlbumService(albumHandler, serviceMetrics);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void when_addAlbum_success_thenReturns() throws Exception {
        StreamRecorder<brgi.grpc.AddAlbumResponse> responseObserver = StreamRecorder.create();

        grpcAlbumService.addAlbum(brgi.grpc.AddAlbumRequest.newBuilder().build(), responseObserver);
        verify(serviceMetrics).recordLatency(eq("add_album"), any(Runnable.class));

    }

    @Test
    void when_getAlbum_success_thenReturns() throws Exception {
        StreamRecorder<brgi.grpc.GetAlbumResponse> responseObserver = StreamRecorder.create();

        grpcAlbumService.getAlbum(brgi.grpc.GetAlbumRequest.newBuilder().build(), responseObserver);
        verify(serviceMetrics).recordLatency(eq("get_album"), any(Runnable.class));

    }

    @Test
    void when_streamAlbum_success_thenReturns() throws Exception {

        StreamRecorder<brgi.grpc.StreamAlbumResponse> responseObserver = StreamRecorder.create();

        grpcAlbumService.streamAlbum(brgi.grpc.StreamAlbumRequest.newBuilder()
                        .setAlbumId(UUID.randomUUID().toString())
                .build(), responseObserver);

        verify(serviceMetrics).recordLatency(eq("stream_album"), any(Runnable.class));

    }


    @Test
    void when_getAllSongs_success_thenReturns() throws Exception {
        StreamRecorder<brgi.grpc.GetAllSongsResponse> responseObserver = StreamRecorder.create();

        grpcAlbumService.getAllSongs(brgi.grpc.GetAllSongsRequest.getDefaultInstance(), responseObserver);
        verify(serviceMetrics).recordLatency(eq("get_all_songs"), any(Runnable.class));


    }


    @Test
    void when_getAlbumArtists_success_thenReturns() throws Exception {
        StreamRecorder<brgi.grpc.GetArtistsResponse> responseObserver = StreamRecorder.create();

        grpcAlbumService.getAlbumArtists(brgi.grpc.GetArtistsRequest.getDefaultInstance(), responseObserver);
        verify(serviceMetrics).recordLatency(eq("get_album_artists"), any(Runnable.class));

    }

    @Test
    void when_deleteAlbum_success_thenReturns() throws Exception {
        StreamRecorder<brgi.grpc.DeleteAlbumResponse> responseObserver = StreamRecorder.create();

        grpcAlbumService.deleteAlbum(brgi.grpc.DeleteAlbumRequest.getDefaultInstance(), responseObserver);
        verify(serviceMetrics).recordLatency(eq("delete_album"), any(Runnable.class));

    }

}