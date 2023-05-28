package com.bragi.bragi.server;

import brgi.grpc.AddSongRequest;
import brgi.grpc.AddSongResponse;
import brgi.grpc.BrgiSongServiceGrpc;
import brgi.grpc.GetSongRequest;
import com.bragi.bragi.metrics.ServiceMetrics;
import com.bragi.bragi.server.handlers.SongHandler;
import com.bragi.bragi.service.SongService;
import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Stream;


@RequiredArgsConstructor
@Service
@Slf4j
public class GrpcSongService extends BrgiSongServiceGrpc.BrgiSongServiceImplBase {

    private final SongHandler songHandler;
    private final ServiceMetrics serviceMetrics;

    @Override
    public void addSong(AddSongRequest request, StreamObserver<AddSongResponse> responseObserver) {
        serviceMetrics.recordLatency("add_song", ()-> songHandler.addSong(request, responseObserver));
    }

    @Override
    public void getSong(GetSongRequest request, StreamObserver<brgi.grpc.GetSongResponse> responseObserver) {
        serviceMetrics.recordLatency("get_song", ()->songHandler.getSong(request, responseObserver));
    }

    @Override
    public void streamSong(brgi.grpc.StreamSongRequest request, StreamObserver<brgi.grpc.StreamSongResponse> responseObserver) {
        serviceMetrics.recordLatency("stream_song", ()->songHandler.streamSong(request, responseObserver));
    }

    @Override
    public void getSongAlbum(brgi.grpc.GetSongAlbumRequest request, StreamObserver<brgi.grpc.GetSongAlbumResponse> responseObserver) {
        serviceMetrics.recordLatency("get_song_album", ()->songHandler.getSongAlbum(request, responseObserver));
    }

    @Override
    public void getSongArtists(brgi.grpc.GetArtistsRequest request, StreamObserver<brgi.grpc.GetArtistsResponse> responseObserver) {
        serviceMetrics.recordLatency("get_song_artists", ()->songHandler.getSongArtists(request, responseObserver));
    }

    @Override
    public void deleteSong(brgi.grpc.DeleteSongRequest request, StreamObserver<brgi.grpc.DeleteSongResponse> responseObserver) {
        serviceMetrics.recordLatency("delete_song", ()->songHandler.deleteSong(request, responseObserver));
    }
}
