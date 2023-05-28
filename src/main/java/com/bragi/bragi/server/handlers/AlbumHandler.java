package com.bragi.bragi.server.handlers;


import brgi.grpc.AddAlbumRequest;
import brgi.grpc.AddAlbumResponse;
import brgi.grpc.GetAlbumRequest;
import brgi.grpc.GetAlbumResponse;
import com.bragi.bragi.metrics.ServiceMetrics;
import com.bragi.bragi.service.AlbumService;
import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlbumHandler {
    
    private final AlbumService albumService;
    private final ServiceMetrics serviceMetrics;

    public void addAlbum(AddAlbumRequest request, StreamObserver<AddAlbumResponse> responseObserver) {
        try{
            responseObserver.onNext(albumService.store(request));
        }catch(Exception e){
            log.error("Error occurred while trying to add album: {}", e.getMessage());
            serviceMetrics.incrementErrorCount("add_album");
            responseObserver.onError(e);
        }
    }

    
    public void getAlbum(GetAlbumRequest request, StreamObserver<GetAlbumResponse> responseObserver) {
        try{
            responseObserver.onNext(albumService.getAlbum(request));
        }catch(Exception e){
            log.error("Error occurred while trying to get album: {}", e.getMessage());
            serviceMetrics.incrementErrorCount("get_album");
            responseObserver.onError(e);
        }
    }

    
    public void streamAlbum(brgi.grpc.StreamAlbumRequest request, StreamObserver<brgi.grpc.StreamAlbumResponse> responseObserver) {
        try{
            albumService.getSongs(UUID.fromString(request.getAlbumId()))
                    .forEach(song-> responseObserver.onNext(brgi.grpc.StreamAlbumResponse.newBuilder()
                            .setContent(ByteString.copyFrom(song.getSongContent().getContent())).build()));
        }catch(Exception e){
            log.error("Error occurred while trying to stream album: {}", e.getMessage());
            serviceMetrics.incrementErrorCount("stream_album");
            responseObserver.onError(e);
        }
    }

    
    public void getAllSongs(brgi.grpc.GetAllSongsRequest request, StreamObserver<brgi.grpc.GetAllSongsResponse> responseObserver) {
        try{
            responseObserver.onNext(albumService.getAllSongs(request));
        }catch(Exception e){
            log.error("Error occurred while trying to get all songs: {}", e.getMessage());
            serviceMetrics.incrementErrorCount("get_all_songs");
            responseObserver.onError(e);
        }
    }

    
    public void getAlbumArtists(brgi.grpc.GetArtistsRequest request, StreamObserver<brgi.grpc.GetArtistsResponse> responseObserver) {
        try{
            responseObserver.onNext(albumService.getArtists(request));
        }catch (Exception e){
            log.error("Error occurred while trying to get all album artists: {}", e.getMessage());
            serviceMetrics.incrementErrorCount("get_album_artists");
            responseObserver.onError(e);
        }
    }

    
    public void deleteAlbum(brgi.grpc.DeleteAlbumRequest request, StreamObserver<brgi.grpc.DeleteAlbumResponse> responseObserver) {
        try{
            responseObserver.onNext(albumService.deleteById(request));
        }catch (Exception e){
            log.error("Error occurred while trying to delete album: {}", e.getMessage());
            serviceMetrics.incrementErrorCount("delete_album");
            responseObserver.onError(e);
        }
    }
    
}
