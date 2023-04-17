package com.bragi.bragi.server;

import brgi.grpc.AddAlbumRequest;
import brgi.grpc.AddAlbumResponse;
import brgi.grpc.GetAlbumRequest;
import brgi.grpc.GetAlbumResponse;
import com.bragi.bragi.service.AlbumService;
import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class GrpcAlbumService extends brgi.grpc.BrgiAlbumServiceGrpc.BrgiAlbumServiceImplBase {


    private final AlbumService albumService;
    @Override
    public void addAlbum(AddAlbumRequest request, StreamObserver<AddAlbumResponse> responseObserver) {
        try{
            responseObserver.onNext(albumService.store(request));
        }catch(Exception e){
            log.error("Error occurred while trying to add album: {}", e.getMessage());
            responseObserver.onError(e);
        }
    }

    @Override
    public void getAlbum(GetAlbumRequest request, StreamObserver<GetAlbumResponse> responseObserver) {
        try{
            responseObserver.onNext(albumService.getAlbum(request));
        }catch(Exception e){
            log.error("Error occurred while trying to get album: {}", e.getMessage());
            responseObserver.onError(e);
        }
    }

    @Override
    public void streamAlbum(brgi.grpc.StreamAlbumRequest request, StreamObserver<brgi.grpc.StreamAlbumResponse> responseObserver) {
        try{
            albumService.getSongs(UUID.fromString(request.getAlbumId()))
                    .forEach(song-> responseObserver.onNext(brgi.grpc.StreamAlbumResponse.newBuilder()
                            .setContent(ByteString.copyFrom(song.getSongContent().getContent())).build()));
        }catch(Exception e){
            log.error("Error occurred while trying to stream album: {}", e.getMessage());
            responseObserver.onError(e);
        }
    }

    @Override
    public void getAllSongs(brgi.grpc.GetAllSongsRequest request, StreamObserver<brgi.grpc.GetAllSongsResponse> responseObserver) {
        try{
            responseObserver.onNext(albumService.getAllSongs(request));
        }catch(Exception e){
            log.error("Error occurred while trying to get all songs: {}", e.getMessage());
            responseObserver.onError(e);
        }
    }

    @Override
    public void getAlbumArtists(brgi.grpc.GetArtistsRequest request, StreamObserver<brgi.grpc.GetArtistsResponse> responseObserver) {
        try{
            responseObserver.onNext(albumService.getArtists(request));
        }catch (Exception e){
            log.error("Error occurred while trying to get all album artists: {}", e.getMessage());
            responseObserver.onError(e);
        }
    }

    @Override
    public void deleteAlbum(brgi.grpc.DeleteAlbumRequest request, StreamObserver<brgi.grpc.DeleteAlbumResponse> responseObserver) {
        try{
            responseObserver.onNext(albumService.deleteById(request));
        }catch (Exception e){
            log.error("Error occurred while trying to delete album: {}", e.getMessage());
            responseObserver.onError(e);
        }
    }
}
