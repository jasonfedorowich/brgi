package com.bragi.bragi.service;

import com.bragi.bragi.model.Album;
import com.bragi.bragi.model.Artist;
import com.bragi.bragi.model.Song;
import com.bragi.bragi.model.SongContent;
import com.bragi.bragi.repository.AlbumRepository;
import com.bragi.bragi.repository.DataAccessService;
import com.bragi.bragi.retry.Retry;
import com.bragi.bragi.service.config.RetryConfig;
import com.bragi.bragi.service.utils.GrpcObjectMapper;
import com.bragi.bragi.service.utils.RetryUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlbumService {

    private final DataAccessService dataAccessService;

    public brgi.grpc.AddAlbumResponse store(brgi.grpc.AddAlbumRequest albumRequest){
       var albumBuilder = Album.builder()
                .title(albumRequest.getAlbum().getTitle())
                .date(new Timestamp(albumRequest.getAlbum().getReleaseDate().getSeconds() * 1_000 +
                        albumRequest.getAlbum().getReleaseDate().getNanos() / 1_000_000));

                var artists = albumRequest.getAlbum()
                        .getArtistIdList()
                        .stream()
                        .map(artistId-> dataAccessService.getArtistByExternalId(UUID.fromString(artistId)))
                        .collect(Collectors.toSet());

               var savedAlbum = dataAccessService.saveAlbum(albumBuilder.artists(artists)
                        .build());

        return brgi.grpc.AddAlbumResponse.newBuilder()
                .setAlbum(albumRequest.getAlbum())
                .setAlbumId(savedAlbum.getExternalId().toString())
                .build();

    }

    public Set<Song> getSongs(UUID externalId){
        var albumFromDb = dataAccessService.getAlbumByExternalId(externalId);
        return albumFromDb.getSongs();
    }



    public brgi.grpc.DeleteAlbumResponse deleteById(brgi.grpc.DeleteAlbumRequest deleteAlbumRequest){
        var album = dataAccessService.getAlbumByExternalId(UUID.fromString(deleteAlbumRequest.getAlbumId()));
        dataAccessService.deleteAlbumById(album.getId());
        return brgi.grpc.DeleteAlbumResponse.getDefaultInstance();
    }

    public brgi.grpc.GetAlbumResponse getAlbum(brgi.grpc.GetAlbumRequest request) {
        var albumId = UUID.fromString(request.getAlbumId());
       var albumFromDb = dataAccessService.getAlbumByExternalId(albumId);

       var album = GrpcObjectMapper.mapAlbum(albumFromDb);
       return brgi.grpc.GetAlbumResponse.newBuilder()
               .setAlbum(album).build();
    }

    public brgi.grpc.GetArtistsResponse getArtists(brgi.grpc.GetArtistsRequest request){
        if(!request.hasAlbumId())
            throw new RuntimeException("Album id is not supplied");
        var albumFromDb = dataAccessService.getAlbumByExternalId(UUID.fromString(request.getAlbumId()));

        var artists = albumFromDb.getArtists()
                .stream()
                .map(GrpcObjectMapper::mapArtist)
                .toList();

        return brgi.grpc.GetArtistsResponse.newBuilder()
                .addAllArtist(artists)
                .build();
    }

    public brgi.grpc.GetAllSongsResponse getAllSongs(brgi.grpc.GetAllSongsRequest request){
        if(!request.hasAlbumId())
            throw new RuntimeException("Album id is not supplied");
        var albumFromDb = dataAccessService.getAlbumByExternalId(UUID.fromString(request.getAlbumId()));

        var songs = albumFromDb.getSongs()
                .stream()
                .map(GrpcObjectMapper::mapSong)
                .toList();
        return brgi.grpc.GetAllSongsResponse.newBuilder()
                .addAllSong(songs)
                .build();

    }


}
