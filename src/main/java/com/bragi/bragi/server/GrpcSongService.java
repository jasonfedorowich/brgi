package com.bragi.bragi.server;

import brgi.grpc.AddSongRequest;
import brgi.grpc.AddSongResponse;
import brgi.grpc.BrgiSongServiceGrpc;
import brgi.grpc.GetSongRequest;
import io.grpc.stub.StreamObserver;

public class GrpcSongService extends BrgiSongServiceGrpc.BrgiSongServiceImplBase {

    @Override
    public void addSong(AddSongRequest request, StreamObserver<AddSongResponse> responseObserver) {
        super.addSong(request, responseObserver);
    }

    @Override
    public void getSong(GetSongRequest request, StreamObserver<brgi.grpc.GetSongResponse> responseObserver) {
        super.getSong(request, responseObserver);
    }

    @Override
    public void streamSong(brgi.grpc.StreamSongRequest request, StreamObserver<brgi.grpc.StreamSongResponse> responseObserver) {
        super.streamSong(request, responseObserver);
    }

    @Override
    public void getSongAlbum(brgi.grpc.GetSongAlbumRequest request, StreamObserver<brgi.grpc.GetSongAlbumResponse> responseObserver) {
        super.getSongAlbum(request, responseObserver);
    }

    @Override
    public void getSongArtists(brgi.grpc.GetArtistsRequest request, StreamObserver<brgi.grpc.GetArtistsResponse> responseObserver) {
        super.getSongArtists(request, responseObserver);
    }

    @Override
    public void deleteSong(brgi.grpc.DeleteSongRequest request, StreamObserver<brgi.grpc.DeleteSongResponse> responseObserver) {
        super.deleteSong(request, responseObserver);
    }
}
