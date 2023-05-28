package com.bragi.bragi.server;

import brgi.grpc.AddArtistRequest;
import brgi.grpc.AddArtistResponse;
import brgi.grpc.BrgiArtistServiceGrpc;
import brgi.grpc.GetArtistRequest;
import com.bragi.bragi.metrics.ServiceMetrics;
import com.bragi.bragi.server.handlers.ArtistHandler;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class GrpcArtistService extends BrgiArtistServiceGrpc.BrgiArtistServiceImplBase {

    private final ArtistHandler artistHandler;
    private final ServiceMetrics serviceMetrics;

    @Override
    public void addArtist(AddArtistRequest request, StreamObserver<AddArtistResponse> responseObserver) {
        serviceMetrics.recordLatency("add_artist", ()-> artistHandler.addArtist(request, responseObserver));
    }

    @Override
    public void getArtist(GetArtistRequest request, StreamObserver<brgi.grpc.GetArtistResponse> responseObserver) {
        serviceMetrics.recordLatency("get_artist", ()-> artistHandler.getArtist(request, responseObserver));
    }

    @Override
    public void streamArtist(brgi.grpc.StreamArtistRequest request, StreamObserver<brgi.grpc.StreamArtistResponse> responseObserver) {
       serviceMetrics.recordLatency("stream_artist", ()-> artistHandler.streamArtist(request, responseObserver));
    }

    @Override
    public void getAllSongs(brgi.grpc.GetAllSongsRequest request, StreamObserver<brgi.grpc.GetAllSongsResponse> responseObserver) {
        serviceMetrics.recordLatency("get_all_songs", ()-> artistHandler.getAllSongs(request, responseObserver));
    }

    @Override
    public void getAllAlbums(brgi.grpc.GetAllAlbumsRequest request, StreamObserver<brgi.grpc.GetAllAlbumsResponse> responseObserver) {
        serviceMetrics.recordLatency("get_all_albums", ()->artistHandler.getAllAlbums(request, responseObserver));
    }

    @Override
    public void deleteArtist(brgi.grpc.DeleteArtistRequest request, StreamObserver<brgi.grpc.DeleteArtistResponse> responseObserver) {
        serviceMetrics.recordLatency("delete_artist", ()->artistHandler.deleteArtist(request, responseObserver));
    }
}
