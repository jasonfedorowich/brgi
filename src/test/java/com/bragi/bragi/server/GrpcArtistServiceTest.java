package com.bragi.bragi.server;

import brgi.grpc.AddArtistRequest;
import brgi.grpc.AddArtistResponse;
import com.bragi.bragi.metrics.ServiceMetrics;
import com.bragi.bragi.metrics.ServiceMetricsBuilder;
import com.bragi.bragi.model.Song;
import com.bragi.bragi.model.SongContent;
import com.bragi.bragi.server.handlers.ArtistHandler;
import com.bragi.bragi.service.ArtistService;
import io.grpc.internal.testing.StreamRecorder;
import io.prometheus.client.Histogram;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockSettings;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrpcArtistServiceTest {

    private GrpcArtistService grpcArtistService;

    @Mock
    private ArtistHandler artistHandler;

    @Mock
    private ServiceMetrics serviceMetrics;

    @BeforeEach
    void setUp(){
        grpcArtistService = new GrpcArtistService(artistHandler, serviceMetrics);
    }

    @Test
    void when_addArtist_success_thenReturns() throws Exception {
        StreamRecorder<AddArtistResponse> responseObserver = StreamRecorder.create();
        grpcArtistService.addArtist(AddArtistRequest.newBuilder().build(), responseObserver);
        verify(serviceMetrics).recordLatency(eq("add_artist"), any(Runnable.class));
    }


    @Test
    void when_getArtist_success_thenReturns() throws Exception {
        StreamRecorder<brgi.grpc.GetArtistResponse> responseObserver = StreamRecorder.create();
        grpcArtistService.getArtist(brgi.grpc.GetArtistRequest.newBuilder().build(), responseObserver);
        verify(serviceMetrics).recordLatency(eq("get_artist"), any(Runnable.class));
    }

    @Test
    void when_streamArtist_success_thenReturns() throws Exception {
        StreamRecorder<brgi.grpc.StreamArtistResponse> responseObserver = StreamRecorder.create();

        grpcArtistService.streamArtist(brgi.grpc.StreamArtistRequest.newBuilder()
                        .setArtistId(UUID.randomUUID().toString())
                .build(), responseObserver);
        verify(serviceMetrics).recordLatency(eq("stream_artist"), any(Runnable.class));
    }


    @Test
    void when_getAllSongs_success_thenReturns() throws Exception {
        StreamRecorder<brgi.grpc.GetAllSongsResponse> responseObserver = StreamRecorder.create();

        grpcArtistService.getAllSongs(brgi.grpc.GetAllSongsRequest.getDefaultInstance(), responseObserver);
        verify(serviceMetrics).recordLatency(eq("get_all_songs"), any(Runnable.class));
    }

    @Test
    void when_getAllAlbums_success_thenReturns() throws Exception {
        StreamRecorder<brgi.grpc.GetAllAlbumsResponse> responseObserver = StreamRecorder.create();

        grpcArtistService.getAllAlbums(brgi.grpc.GetAllAlbumsRequest.getDefaultInstance(), responseObserver);
        verify(serviceMetrics).recordLatency(eq("get_all_albums"), any(Runnable.class));
    }


    @Test
    void when_deleteArtist_success_thenReturns() throws Exception {
        StreamRecorder<brgi.grpc.DeleteArtistResponse> responseObserver = StreamRecorder.create();

        grpcArtistService.deleteArtist(brgi.grpc.DeleteArtistRequest.getDefaultInstance(), responseObserver);
        verify(serviceMetrics).recordLatency(eq("delete_artist"), any(Runnable.class));

    }

}