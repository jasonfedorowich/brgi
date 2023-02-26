package com.bragi.bragi.service.utils;

import com.bragi.bragi.model.Album;
import com.bragi.bragi.model.Artist;
import com.bragi.bragi.model.Song;
import com.google.protobuf.ByteString;

import java.util.stream.Collectors;

public class GrpcObjectMapper {


    public static brgi.grpc.Album mapAlbum(Album album){
        var artistIds = album
                .getArtists()
                .stream()
                .map(artist-> artist.getExternalId().toString()).collect(Collectors.toSet());

        var songIds = album
                .getSongs()
                .stream()
                .map(song-> song.getExternalId().toString()).collect(Collectors.toSet());



        return brgi.grpc.Album.newBuilder()
                .addAllArtistId(artistIds)
                .addAllSongId(songIds)
                .setAlbumId(album.getExternalId().toString())
                .setTitle(album.getTitle())
                .setReleaseDate(com.google.protobuf.Timestamp.newBuilder()
                        .setNanos(album.getDate().getNanos())
                        .setSeconds(album.getDate().getSeconds())).build();

    }

    public static brgi.grpc.Song mapSong(Song song){
        var artistIds = song.getArtists()
                .stream()
                .map(artist-> artist.getExternalId().toString()).collect(Collectors.toSet());

        var content = song.getSongContent().getContent();

        return brgi.grpc.Song.newBuilder()
                .setContent(ByteString.copyFrom(content))
                .setDuration(song.getDuration())
                .setTitle(song.getTitle())
                .setSongId(song.getExternalId().toString())
                .setAlbumId(song.getAlbum().getExternalId().toString())
                .addAllArtistId(artistIds)
                .setReleaseDate(com.google.protobuf.Timestamp.newBuilder()
                        .setNanos(song.getDateReleased().getNanos())
                        .setSeconds(song.getDateReleased().getSeconds()))
                .build();
    }

    public static brgi.grpc.Artist mapArtist(Artist artist){
        var songIds = artist.getSongs()
                .stream()
                .map(song -> song.getExternalId().toString())
                .collect(Collectors.toSet());

        var albumIds = artist.getAlbums()
                .stream()
                .map(album -> album.getExternalId().toString())
                .collect(Collectors.toSet());

        return brgi.grpc.Artist
                .newBuilder()
                .setArtistId(artist.getExternalId().toString())
                .addAllSongId(songIds)
                .addAllAlbumId(albumIds)
                .setTimeFormed(com.google.protobuf.Timestamp.newBuilder()
                        .setNanos(artist.getTimeStarted().getNanos())
                        .setSeconds(artist.getTimeStarted().getSeconds()))
                .setName(artist.getName()).build();
    }
}
