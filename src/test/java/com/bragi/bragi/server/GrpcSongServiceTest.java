package com.bragi.bragi.server;

import brgi.grpc.*;
import com.bragi.bragi.metrics.ServiceMetrics;
import com.bragi.bragi.model.Song;
import com.bragi.bragi.model.SongContent;
import com.bragi.bragi.server.handlers.SongHandler;
import com.bragi.bragi.service.SongService;
import com.google.protobuf.ByteString;
import io.grpc.internal.testing.StreamRecorder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GrpcSongServiceTest {

    @Mock
    private SongHandler songHandler;

    @Mock
    private ServiceMetrics serviceMetrics;

    private GrpcSongService grpcSongService;

    @BeforeEach
    void setUp() {
        grpcSongService = new GrpcSongService(songHandler, serviceMetrics);
    }

    @Test
    void when_addSong_success_thenReturns() throws Exception {
        StreamRecorder<AddSongResponse> responseObserver = StreamRecorder.create();
        grpcSongService.addSong(AddSongRequest.newBuilder().build(), responseObserver);
        verify(serviceMetrics).recordLatency(eq("add_song"), any(Runnable.class));
    }

    @Test
    void when_getSong_success_thenReturns() throws Exception {
        StreamRecorder<GetSongResponse> responseObserver = StreamRecorder.create();
        grpcSongService.getSong(GetSongRequest.newBuilder().build(), responseObserver);
        verify(serviceMetrics).recordLatency(eq("get_song"), any(Runnable.class));
    }


    @Test
    void when_streamSong_success_thenReturns() throws Exception {
        StreamRecorder<StreamSongResponse> responseObserver = StreamRecorder.create();
        grpcSongService.streamSong(StreamSongRequest.newBuilder().build(), responseObserver);
        verify(serviceMetrics).recordLatency(eq("stream_song"), any(Runnable.class));

    }

    @Test
    void when_getSongAlbum_success_thenReturns() throws Exception {
        StreamRecorder<GetSongAlbumResponse> responseObserver = StreamRecorder.create();
        grpcSongService.getSongAlbum(GetSongAlbumRequest.newBuilder().build(), responseObserver);
        verify(serviceMetrics).recordLatency(eq("get_song_album"), any(Runnable.class));
    }


    @Test
    void when_getSongArtists_success_thenReturns() throws Exception {
        StreamRecorder<GetArtistsResponse> responseObserver = StreamRecorder.create();
        grpcSongService.getSongArtists(GetArtistsRequest.newBuilder().build(), responseObserver);
        verify(serviceMetrics).recordLatency(eq("get_song_artists"), any(Runnable.class));
    }

    @Test
    void when_deleteSong_success_thenReturns() throws Exception {
        StreamRecorder<DeleteSongResponse> responseObserver = StreamRecorder.create();
        grpcSongService.deleteSong(DeleteSongRequest.newBuilder().build(), responseObserver);
        verify(serviceMetrics).recordLatency(eq("delete_song"), any(Runnable.class));
    }

}