package com.bragi.bragi.server.handlers;

import brgi.grpc.AddArtistRequest;
import brgi.grpc.AddArtistResponse;
import brgi.grpc.DeleteArtistRequest;
import brgi.grpc.GetArtistRequest;
import com.bragi.bragi.metrics.ServiceMetrics;
import com.bragi.bragi.metrics.ServiceMetricsBuilder;
import com.bragi.bragi.model.Song;
import com.bragi.bragi.model.SongContent;
import com.bragi.bragi.service.ArtistService;
import io.grpc.internal.testing.StreamRecorder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ArtistHandlerTest {

    private ArtistHandler artistHandler;

    @Mock
    private ArtistService artistService;

    @Mock
    private ServiceMetrics serviceMetrics;

    @Mock
    private ServiceMetricsBuilder serviceMetricsBuilder;

    @BeforeEach
    void setUp(){
        artistHandler = new ArtistHandler(artistService, serviceMetrics);
    }

    @Test
    void when_addArtist_success_thenReturns() throws Exception {
        var expected = brgi.grpc.AddArtistResponse.newBuilder()
                .setArtistId("new-artist-id").build();
        when(artistService.store(any(AddArtistRequest.class))).thenReturn(
                expected);

        StreamRecorder<AddArtistResponse> responseObserver = StreamRecorder.create();
        artistHandler.addArtist(brgi.grpc.AddArtistRequest.newBuilder().build(), responseObserver);
        responseObserver.awaitCompletion(1000, TimeUnit.MILLISECONDS);
        var response = responseObserver.getValues().get(0);
        assertEquals(expected, response);
        verify(artistService).store(any(AddArtistRequest.class));

    }

    @Test
    void when_addArtist_fails_thenThrows() throws Exception {
        when(artistService.store(any(AddArtistRequest.class))).thenThrow(new RuntimeException());

        StreamRecorder<brgi.grpc.AddArtistResponse> responseObserver = StreamRecorder.create();
        artistHandler.addArtist(brgi.grpc.AddArtistRequest.newBuilder().build(), responseObserver);

        responseObserver.awaitCompletion(1000, TimeUnit.MILLISECONDS);

        var error = responseObserver.getError();
        verify(serviceMetrics).incrementErrorCount("add_artist");
        assertTrue(error instanceof RuntimeException);

    }

    @Test
    void when_getArtist_success_thenReturns() throws Exception {
        var expected = brgi.grpc.GetArtistResponse.newBuilder().build();
        when(artistService.getArtist(any(GetArtistRequest.class))).thenReturn(expected);

        StreamRecorder<brgi.grpc.GetArtistResponse> responseObserver = StreamRecorder.create();
        artistHandler.getArtist(brgi.grpc.GetArtistRequest.newBuilder().build(), responseObserver);

        responseObserver.awaitCompletion(1000, TimeUnit.MILLISECONDS);

        var response = responseObserver.getValues().get(0);
        assertEquals(expected, response);

    }

    @Test
    void when_getArtist_fails_thenThrows() throws Exception {
        var expected = brgi.grpc.GetArtistResponse.newBuilder().build();
        when(artistService.getArtist(any(GetArtistRequest.class))).thenThrow(new RuntimeException());

        StreamRecorder<brgi.grpc.GetArtistResponse> responseObserver = StreamRecorder.create();
        artistHandler.getArtist(brgi.grpc.GetArtistRequest.newBuilder().build(), responseObserver);

        responseObserver.awaitCompletion(1000, TimeUnit.MILLISECONDS);

        var error = responseObserver.getError();
        verify(serviceMetrics).incrementErrorCount("get_artist");
        assertTrue(error instanceof RuntimeException);

    }

    @Test
    void when_streamArtist_success_thenReturns() throws Exception {
        var content = new byte[]{1, 2, 3};

        when(artistService.getSongs(any()))
                .thenReturn(Set.of(Song.builder().songContent(SongContent.builder().content(content)
                        .build()).build()));

        StreamRecorder<brgi.grpc.StreamArtistResponse> responseObserver = StreamRecorder.create();

        artistHandler.streamArtist(brgi.grpc.StreamArtistRequest.newBuilder()
                .setArtistId(UUID.randomUUID().toString())
                .build(), responseObserver);

        responseObserver.awaitCompletion(1000, TimeUnit.MILLISECONDS);

        var response = responseObserver.getValues().get(0).getContent().toByteArray();
        assertEquals(content[0], response[0]);
        assertEquals(content[1], response[1]);
        assertEquals(content[2], response[2]);


    }

    @Test
    void when_streamArtist_fails_thenThrows() throws Exception {
        when(artistService.getSongs(any()))
                .thenThrow(new RuntimeException());

        StreamRecorder<brgi.grpc.StreamArtistResponse> responseObserver = StreamRecorder.create();

        artistHandler.streamArtist(brgi.grpc.StreamArtistRequest.newBuilder()
                .setArtistId(UUID.randomUUID().toString())
                .build(), responseObserver);

        responseObserver.awaitCompletion(1000, TimeUnit.MILLISECONDS);

        var error = responseObserver.getError();
        verify(serviceMetrics).incrementErrorCount("stream_artist");
        assertTrue(error instanceof RuntimeException);

    }

    @Test
    void when_getAllSongs_success_thenReturns() throws Exception {
        var song = brgi.grpc.Song.newBuilder().build();
        var expected = brgi.grpc.GetAllSongsResponse.newBuilder()
                .addAllSong(List.of(song)).build();
        when(artistService.getAllSongs(any()))
                .thenReturn(expected);
        StreamRecorder<brgi.grpc.GetAllSongsResponse> responseObserver = StreamRecorder.create();

        artistHandler.getAllSongs(brgi.grpc.GetAllSongsRequest.getDefaultInstance(), responseObserver);
        responseObserver.awaitCompletion(1000, TimeUnit.MILLISECONDS);

        var actual = responseObserver.getValues().get(0);
        assertEquals(expected, actual);
    }

    @Test
    void when_getAllSongs_fails_thenThrows() throws Exception {
        var song = brgi.grpc.Song.newBuilder().build();
        var expected = brgi.grpc.GetAllSongsResponse.newBuilder()
                .addAllSong(List.of(song)).build();
        when(artistService.getAllSongs(any()))
                .thenThrow(new RuntimeException());
        StreamRecorder<brgi.grpc.GetAllSongsResponse> responseObserver = StreamRecorder.create();

        artistHandler.getAllSongs(brgi.grpc.GetAllSongsRequest.getDefaultInstance(), responseObserver);
        responseObserver.awaitCompletion(1000, TimeUnit.MILLISECONDS);

        var actual = responseObserver.getError();
        verify(serviceMetrics).incrementErrorCount("get_all_songs");
        assertTrue(actual instanceof RuntimeException);
    }

    @Test
    void when_getAllAlbums_success_thenReturns() throws Exception {
        var expected = brgi.grpc.GetAllAlbumsResponse.getDefaultInstance();
        when(artistService.getAllAlbums(any()))
                .thenReturn(expected);
        StreamRecorder<brgi.grpc.GetAllAlbumsResponse> responseObserver = StreamRecorder.create();

        artistHandler.getAllAlbums(brgi.grpc.GetAllAlbumsRequest.getDefaultInstance(), responseObserver);
        responseObserver.awaitCompletion(1000, TimeUnit.MILLISECONDS);

        var actual = responseObserver.getValues().get(0);
        assertEquals(expected, actual);
    }

    @Test
    void when_getAllAlbums_fails_thenThrows() throws Exception {
        when(artistService.getAllAlbums(any()))
                .thenThrow(new RuntimeException());
        StreamRecorder<brgi.grpc.GetAllAlbumsResponse> responseObserver = StreamRecorder.create();

        artistHandler.getAllAlbums(brgi.grpc.GetAllAlbumsRequest.getDefaultInstance(), responseObserver);
        responseObserver.awaitCompletion(1000, TimeUnit.MILLISECONDS);

        var actual = responseObserver.getError();
        verify(serviceMetrics).incrementErrorCount("get_all_albums");
        assertTrue(actual instanceof RuntimeException);
    }

    @Test
    void when_deleteArtist_success_thenReturns() throws Exception {
        var expected = brgi.grpc.DeleteArtistResponse.getDefaultInstance();
        when(artistService.deleteArtist(any(DeleteArtistRequest.class)))
                .thenReturn(expected);
        StreamRecorder<brgi.grpc.DeleteArtistResponse> responseObserver = StreamRecorder.create();

        artistHandler.deleteArtist(brgi.grpc.DeleteArtistRequest.getDefaultInstance(), responseObserver);
        responseObserver.awaitCompletion(1000, TimeUnit.MILLISECONDS);

        var actual = responseObserver.getValues().get(0);
        assertEquals(expected, actual);

    }

    @Test
    void when_deleteArtist_fails_thenThrows() throws Exception {
        var expected = brgi.grpc.DeleteArtistResponse.getDefaultInstance();
        when(artistService.deleteArtist(any(DeleteArtistRequest.class)))
                .thenThrow(new RuntimeException());
        StreamRecorder<brgi.grpc.DeleteArtistResponse> responseObserver = StreamRecorder.create();

        artistHandler.deleteArtist(brgi.grpc.DeleteArtistRequest.getDefaultInstance(), responseObserver);
        responseObserver.awaitCompletion(1000, TimeUnit.MILLISECONDS);

        var actual = responseObserver.getError();
        verify(serviceMetrics).incrementErrorCount("delete_artist");
        assertTrue(actual instanceof RuntimeException);

    }
}
