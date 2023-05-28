package com.bragi.bragi.server.handlers;


import brgi.grpc.AddArtistRequest;
import brgi.grpc.AddArtistResponse;
import brgi.grpc.GetArtistRequest;
import com.bragi.bragi.metrics.ServiceMetrics;
import com.bragi.bragi.service.ArtistService;
import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArtistHandler {

    private final ArtistService artistService;
    private final ServiceMetrics serviceMetrics;

    public void addArtist(AddArtistRequest request, StreamObserver<AddArtistResponse> responseObserver) {
        try{
            responseObserver.onNext(artistService.store(request));
        }catch(Exception e){
            log.error("Failed to add artist with: {}", e.getMessage());
            serviceMetrics.incrementErrorCount("add_artist");
            responseObserver.onError(new RuntimeException("Cannot save artist", e));
        }

    }

    public void getArtist(GetArtistRequest request, StreamObserver<brgi.grpc.GetArtistResponse> responseObserver) {
        try{
            responseObserver.onNext(artistService.getArtist(request));
        }catch (Exception e){
            log.error("Failed to get artist with: {}", e.getMessage());
            serviceMetrics.incrementErrorCount("get_artist");
            responseObserver.onError(new RuntimeException("Cannot get artist", e));
        }
    }

    public void streamArtist(brgi.grpc.StreamArtistRequest request, StreamObserver<brgi.grpc.StreamArtistResponse> responseObserver) {
        try{
            artistService.getSongs(UUID.fromString(request.getArtistId()))
                    .forEach(song -> {
                        responseObserver.onNext(brgi.grpc.StreamArtistResponse.newBuilder()
                                .setContent(ByteString.copyFrom(song.getSongContent().getContent()))
                                .build());
                    });
        }catch (Exception e){
            log.error("Failed to stream artist with: {}", e.getMessage());
            serviceMetrics.incrementErrorCount("stream_artist");
            responseObserver.onError(new RuntimeException("Cannot stream artist", e));
        }finally {
            responseObserver.onCompleted();
        }
    }

    public void getAllSongs(brgi.grpc.GetAllSongsRequest request, StreamObserver<brgi.grpc.GetAllSongsResponse> responseObserver) {
        try{
            responseObserver.onNext(artistService.getAllSongs(request));
        }catch(Exception e){
            log.error("Failed to get all songs with: {}", e.getMessage());
            serviceMetrics.incrementErrorCount("get_all_songs");
            responseObserver.onError(new RuntimeException("Cannot stream artist", e));
        }
    }

    public void getAllAlbums(brgi.grpc.GetAllAlbumsRequest request, StreamObserver<brgi.grpc.GetAllAlbumsResponse> responseObserver) {
        try{
            responseObserver.onNext(artistService.getAllAlbums(request));
        }catch(Exception e){
            log.error("Failed to get all albums with: {}", e.getMessage());
            serviceMetrics.incrementErrorCount("get_all_albums");
            responseObserver.onError(new RuntimeException("Cannot stream artist", e));
        }
    }

    public void deleteArtist(brgi.grpc.DeleteArtistRequest request, StreamObserver<brgi.grpc.DeleteArtistResponse> responseObserver) {
        try{
            responseObserver.onNext(artistService.deleteArtist(request));
        }catch(Exception e){
            log.error("Failed to delete artist with: {}", e.getMessage());
            serviceMetrics.incrementErrorCount("delete_artist");
            responseObserver.onError(new RuntimeException("Cannot stream artist", e));
        }
    }
}
