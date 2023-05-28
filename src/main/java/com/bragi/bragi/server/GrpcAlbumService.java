package com.bragi.bragi.server;

import brgi.grpc.AddAlbumRequest;
import brgi.grpc.AddAlbumResponse;
import brgi.grpc.GetAlbumRequest;
import brgi.grpc.GetAlbumResponse;
import com.bragi.bragi.metrics.ServiceMetrics;
import com.bragi.bragi.server.handlers.AlbumHandler;
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


    private final AlbumHandler albumHandler;
    private final ServiceMetrics serviceMetrics;
    @Override
    public void addAlbum(AddAlbumRequest request, StreamObserver<AddAlbumResponse> responseObserver) {
        serviceMetrics.recordLatency("add_album", ()-> albumHandler.addAlbum(request, responseObserver));
    }

    @Override
    public void getAlbum(GetAlbumRequest request, StreamObserver<GetAlbumResponse> responseObserver) {
        serviceMetrics.recordLatency("get_album", ()-> albumHandler.getAlbum(request, responseObserver));
    }

    @Override
    public void streamAlbum(brgi.grpc.StreamAlbumRequest request, StreamObserver<brgi.grpc.StreamAlbumResponse> responseObserver) {
        serviceMetrics.recordLatency("stream_album", ()-> albumHandler.streamAlbum(request, responseObserver));
    }

    @Override
    public void getAllSongs(brgi.grpc.GetAllSongsRequest request, StreamObserver<brgi.grpc.GetAllSongsResponse> responseObserver) {
       serviceMetrics.recordLatency("get_all_songs", ()-> albumHandler.getAllSongs(request, responseObserver));
    }

    @Override
    public void getAlbumArtists(brgi.grpc.GetArtistsRequest request, StreamObserver<brgi.grpc.GetArtistsResponse> responseObserver) {
        serviceMetrics.recordLatency("get_album_artists", ()-> albumHandler.getAlbumArtists(request, responseObserver));
    }

    @Override
    public void deleteAlbum(brgi.grpc.DeleteAlbumRequest request, StreamObserver<brgi.grpc.DeleteAlbumResponse> responseObserver) {
        serviceMetrics.recordLatency("delete_album", ()-> albumHandler.deleteAlbum(request, responseObserver));
    }
}
