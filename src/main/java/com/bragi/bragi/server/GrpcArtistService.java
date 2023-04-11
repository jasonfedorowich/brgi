package com.bragi.bragi.server;

import brgi.grpc.AddArtistRequest;
import brgi.grpc.AddArtistResponse;
import brgi.grpc.BrgiArtistServiceGrpc;
import brgi.grpc.GetArtistRequest;
import com.bragi.bragi.service.ArtistService;
import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class GrpcArtistService extends BrgiArtistServiceGrpc.BrgiArtistServiceImplBase {

    private final ArtistService artistService;

    @Override
    public void addArtist(AddArtistRequest request, StreamObserver<AddArtistResponse> responseObserver) {
        try{
            responseObserver.onNext(artistService.store(request));
        }catch(Exception e){
            log.error("Failed to add artist with: {}", e.getMessage());
            responseObserver.onError(new RuntimeException("Cannot save artist", e));
        }
    }

    @Override
    public void getArtist(GetArtistRequest request, StreamObserver<brgi.grpc.GetArtistResponse> responseObserver) {
        try{
            responseObserver.onNext(artistService.getArtist(request));
        }catch (Exception e){
            log.error("Failed to get artist with: {}", e.getMessage());
            responseObserver.onError(new RuntimeException("Cannot get artist", e));
        }
    }

    @Override
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
            responseObserver.onError(new RuntimeException("Cannot stream artist", e));
        }finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getAllSongs(brgi.grpc.GetAllSongsRequest request, StreamObserver<brgi.grpc.GetAllSongsResponse> responseObserver) {
        try{
            responseObserver.onNext(artistService.getAllSongs(request));
        }catch(Exception e){
            log.error("Failed to get all songs with: {}", e.getMessage());
            responseObserver.onError(new RuntimeException("Cannot stream artist", e));
        }
    }

    @Override
    public void getAllAlbums(brgi.grpc.GetAllAlbumsRequest request, StreamObserver<brgi.grpc.GetAllAlbumsResponse> responseObserver) {
        try{
            responseObserver.onNext(artistService.getAllAlbums(request));
        }catch(Exception e){
            log.error("Failed to get all albums with: {}", e.getMessage());
            responseObserver.onError(new RuntimeException("Cannot stream artist", e));
        }
    }

    @Override
    public void deleteArtist(brgi.grpc.DeleteArtistRequest request, StreamObserver<brgi.grpc.DeleteArtistResponse> responseObserver) {
        try{
            responseObserver.onNext(artistService.deleteArtist(request));
        }catch(Exception e){
            log.error("Failed to delete artist with: {}", e.getMessage());
            responseObserver.onError(new RuntimeException("Cannot stream artist", e));
        }
    }
}
