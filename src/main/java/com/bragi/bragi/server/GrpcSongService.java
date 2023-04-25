package com.bragi.bragi.server;

import brgi.grpc.AddSongRequest;
import brgi.grpc.AddSongResponse;
import brgi.grpc.BrgiSongServiceGrpc;
import brgi.grpc.GetSongRequest;
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

    private final SongService songService;

    @Override
    public void addSong(AddSongRequest request, StreamObserver<AddSongResponse> responseObserver) {
        try{
            responseObserver.onNext(songService.store(request));
        }catch(Exception e){
            log.error("Error received from adding song: {}", e.getMessage());
            responseObserver.onError(e);
        }
    }

    @Override
    public void getSong(GetSongRequest request, StreamObserver<brgi.grpc.GetSongResponse> responseObserver) {
        try{
            responseObserver.onNext(songService.getSong(request));
        }catch (Exception e){
            log.error("Error received from getting song: {}", e.getMessage());
            responseObserver.onError(e);
        }
    }

    @Override
    public void streamSong(brgi.grpc.StreamSongRequest request, StreamObserver<brgi.grpc.StreamSongResponse> responseObserver) {
        try{
            var song = songService.getSong(UUID.fromString(request.getSongId()));
            var songContent = song.getSongContent();
            responseObserver.onNext(brgi.grpc.StreamSongResponse.newBuilder()
                    .setContent(ByteString.copyFrom(songContent.getContent())).build());

        }catch(Exception e){
            log.error("Error received from streaming song: {}", e.getMessage());
            responseObserver.onError(e);
        }
    }

    @Override
    public void getSongAlbum(brgi.grpc.GetSongAlbumRequest request, StreamObserver<brgi.grpc.GetSongAlbumResponse> responseObserver) {
        try{
            responseObserver.onNext(songService.getSongAlbum(request));
        }catch (Exception e){
            log.error("Error received from getting song album: {}", e.getMessage());
            responseObserver.onError(e);
        }
    }

    @Override
    public void getSongArtists(brgi.grpc.GetArtistsRequest request, StreamObserver<brgi.grpc.GetArtistsResponse> responseObserver) {
        try{
            responseObserver.onNext(songService.getSongsArtists(request));
        }catch (Exception e){
            log.error("Error received from getting song artists: {}", e.getMessage());
            responseObserver.onError(e);
        }
    }

    @Override
    public void deleteSong(brgi.grpc.DeleteSongRequest request, StreamObserver<brgi.grpc.DeleteSongResponse> responseObserver) {
        try{
            responseObserver.onNext(songService.deleteSong(request));
        }catch (Exception e){
            log.error("Error received from deleting song: {}", e.getMessage());
            responseObserver.onError(e);
        }
    }
}
