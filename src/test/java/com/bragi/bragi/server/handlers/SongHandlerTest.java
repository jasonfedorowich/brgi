package com.bragi.bragi.server.handlers;

import brgi.grpc.AddSongResponse;
import com.bragi.bragi.metrics.ServiceMetrics;
import com.bragi.bragi.model.Song;
import com.bragi.bragi.model.SongContent;
import com.bragi.bragi.server.GrpcSongService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SongHandlerTest {


    @Mock
    private SongService songService;

    @Mock
    private ServiceMetrics serviceMetrics;

    private SongHandler songHandler;

    @BeforeEach
    void setUp() {
        songHandler = new SongHandler(songService, serviceMetrics);
    }

    @Test
    void when_addSong_success_thenReturns() throws Exception {
        when(songService.store(any()))
                .thenReturn(brgi.grpc.AddSongResponse.getDefaultInstance());

        StreamRecorder<AddSongResponse> responseObserver = StreamRecorder.create();
        songHandler.addSong(brgi.grpc.AddSongRequest.newBuilder().build(), responseObserver);

        responseObserver.awaitCompletion(1000, TimeUnit.MILLISECONDS);

        var actual = responseObserver.getValues().get(0);
        assertEquals(brgi.grpc.AddSongResponse.getDefaultInstance(), actual);
    }

    @Test
    void when_addSong_fails_thenThrows() throws Exception {
        when(songService.store(any()))
                .thenThrow(new RuntimeException());

        StreamRecorder<brgi.grpc.AddSongResponse> responseObserver = StreamRecorder.create();
        songHandler.addSong(brgi.grpc.AddSongRequest.newBuilder().build(), responseObserver);

        responseObserver.awaitCompletion(1000, TimeUnit.MILLISECONDS);

        var actual = responseObserver.getError();
        assertTrue(actual instanceof RuntimeException);
    }


    @Test
    void when_getSong_success_thenReturns() throws Exception {
        when(songService.getSong(any(brgi.grpc.GetSongRequest.class)))
                .thenReturn(brgi.grpc.GetSongResponse.getDefaultInstance());

        StreamRecorder<brgi.grpc.GetSongResponse> responseObserver = StreamRecorder.create();

        songHandler.getSong(brgi.grpc.GetSongRequest.getDefaultInstance(), responseObserver);
        responseObserver.awaitCompletion(1000, TimeUnit.MILLISECONDS);

        var actual = responseObserver.getValues().get(0);
        assertEquals(brgi.grpc.GetSongResponse.getDefaultInstance(), actual);
    }

    @Test
    void when_getSong_fails_thenThrows() throws Exception {
        when(songService.getSong(any(brgi.grpc.GetSongRequest.class)))
                .thenThrow(new RuntimeException());

        StreamRecorder<brgi.grpc.GetSongResponse> responseObserver = StreamRecorder.create();

        songHandler.getSong(brgi.grpc.GetSongRequest.getDefaultInstance(), responseObserver);
        responseObserver.awaitCompletion(1000, TimeUnit.MILLISECONDS);

        var actual = responseObserver.getError();
        assertTrue(actual instanceof RuntimeException);
    }


    @Test
    void when_streamSong_success_thenReturns() throws Exception {
        when(songService.getSong(any(UUID.class)))
                .thenReturn(Song.builder()
                        .songContent(SongContent.builder()
                                .content(new byte[]{1, 2, 3})
                                .build())
                        .build());

        StreamRecorder<brgi.grpc.StreamSongResponse> responseObserver = StreamRecorder.create();
        songHandler.streamSong(brgi.grpc.StreamSongRequest.newBuilder()
                .setSongId(UUID.randomUUID().toString()).build(), responseObserver);
        responseObserver.awaitCompletion(1000, TimeUnit.MILLISECONDS);

        var actual = responseObserver.getValues().get(0);
        assertEquals(ByteString.copyFrom(new byte[]{1, 2, 3}), actual.getContent());

    }

    @Test
    void when_streamSong_fails_thenThrows() throws Exception {
        when(songService.getSong(any(UUID.class)))
                .thenThrow(new RuntimeException());

        StreamRecorder<brgi.grpc.StreamSongResponse> responseObserver = StreamRecorder.create();
        songHandler.streamSong(brgi.grpc.StreamSongRequest.newBuilder()
                .setSongId(UUID.randomUUID().toString()).build(), responseObserver);
        responseObserver.awaitCompletion(1000, TimeUnit.MILLISECONDS);

        var actual = responseObserver.getError();
        assertTrue(actual instanceof RuntimeException);

    }


    @Test
    void when_getSongAlbum_success_thenReturns() throws Exception {
        when(songService.getSongAlbum(any()))
                .thenReturn(brgi.grpc.GetSongAlbumResponse.getDefaultInstance());

        StreamRecorder<brgi.grpc.GetSongAlbumResponse> responseObserver = StreamRecorder.create();
        songHandler.getSongAlbum(brgi.grpc.GetSongAlbumRequest.getDefaultInstance(), responseObserver);
        responseObserver.awaitCompletion(1000, TimeUnit.MILLISECONDS);

        var actual = responseObserver.getValues().get(0);
        assertEquals(brgi.grpc.GetSongAlbumResponse.getDefaultInstance(), actual);

    }

    @Test
    void when_getSongAlbum_fails_thenThrows() throws Exception {
        when(songService.getSongAlbum(any()))
                .thenThrow(new RuntimeException());

        StreamRecorder<brgi.grpc.GetSongAlbumResponse> responseObserver = StreamRecorder.create();
        songHandler.getSongAlbum(brgi.grpc.GetSongAlbumRequest.getDefaultInstance(), responseObserver);
        responseObserver.awaitCompletion(1000, TimeUnit.MILLISECONDS);

        var actual = responseObserver.getError();
        assertTrue(actual instanceof RuntimeException);

    }

    @Test
    void when_getSongArtists_success_thenReturns() throws Exception {
        when(songService.getSongsArtists(any()))
                .thenReturn(brgi.grpc.GetArtistsResponse.getDefaultInstance());

        StreamRecorder<brgi.grpc.GetArtistsResponse> responseObserver = StreamRecorder.create();
        songHandler.getSongArtists(brgi.grpc.GetArtistsRequest.getDefaultInstance(), responseObserver);
        responseObserver.awaitCompletion(1000, TimeUnit.MILLISECONDS);

        var actual = responseObserver.getValues().get(0);
        assertEquals(brgi.grpc.GetArtistsResponse.getDefaultInstance(), actual);
    }

    @Test
    void when_getSongArtists_fails_thenThrows() throws Exception {
        when(songService.getSongsArtists(any()))
                .thenThrow(new RuntimeException());

        StreamRecorder<brgi.grpc.GetArtistsResponse> responseObserver = StreamRecorder.create();
        songHandler.getSongArtists(brgi.grpc.GetArtistsRequest.getDefaultInstance(), responseObserver);
        responseObserver.awaitCompletion(1000, TimeUnit.MILLISECONDS);

        var actual = responseObserver.getError();
        assertTrue(actual instanceof RuntimeException);
    }

    @Test
    void when_deleteSong_success_thenReturns() throws Exception {
        when(songService.deleteSong(any(brgi.grpc.DeleteSongRequest.class)))
                .thenReturn(brgi.grpc.DeleteSongResponse.getDefaultInstance());

        StreamRecorder<brgi.grpc.DeleteSongResponse> responseObserver = StreamRecorder.create();
        songHandler.deleteSong(brgi.grpc.DeleteSongRequest.getDefaultInstance(), responseObserver);
        responseObserver.awaitCompletion(1000, TimeUnit.MILLISECONDS);

        var actual = responseObserver.getValues().get(0);
        assertEquals(brgi.grpc.DeleteSongResponse.getDefaultInstance(), actual);
    }

    @Test
    void when_deleteSong_fails_thenThrows() throws Exception {
        when(songService.deleteSong(any(brgi.grpc.DeleteSongRequest.class)))
                .thenThrow(new RuntimeException());

        StreamRecorder<brgi.grpc.DeleteSongResponse> responseObserver = StreamRecorder.create();
        songHandler.deleteSong(brgi.grpc.DeleteSongRequest.getDefaultInstance(), responseObserver);
        responseObserver.awaitCompletion(1000, TimeUnit.MILLISECONDS);

        var actual = responseObserver.getError();
        assertTrue(actual instanceof RuntimeException);
    }
}
