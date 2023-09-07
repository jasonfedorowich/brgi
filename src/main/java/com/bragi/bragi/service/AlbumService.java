package com.bragi.bragi.service;

import brgi.grpc.AddAlbumResponse;
import brgi.grpc.GetAlbumResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public List<Album> findAllAlbums(int offset, int size, String sort) {
        Sort sortDirection;
        if(sort.equals("asc")){
            sortDirection = Sort.by("id").ascending();
        }else if(sort.equals("desc")){
            sortDirection = Sort.by("id").descending();
        }else{
            throw new RuntimeException("Invalid sort order must be desc or asc");
        }
        if(size > 10000 || size < 0)
            throw new RuntimeException(String.format("Invalid size provided: %d", size));

        Pageable pageable = PageRequest.of(offset, size, sortDirection);
        Page<Album> albumPage = dataAccessService.findAllAlbums(pageable);
        return albumPage.getContent();
    }

    public Album getAlbum(String id) {
        return dataAccessService.getAlbumByExternalId(UUID.fromString(id));
    }

    public Album store(com.bragi.bragi.rest.dto.Album album) {
        var albumEntity = com.bragi.bragi.model.Album.builder()
                .title(album.getTitle())
                .date(album.getDate())
                .artists(album.getArtists()
                        .stream().map(dataAccessService::getArtistByExternalId).collect(Collectors.toSet()))
                .build();
        return dataAccessService.saveAlbum(albumEntity);

    }

    public void deleteAlbum(String id) {
        Album album;
        try{
            album = dataAccessService.getAlbumByExternalId(UUID.fromString(id));
        }catch (NoSuchElementException e){
            throw new RuntimeException("No album found to delete");
        }
        dataAccessService.deleteAlbumById(album.getId());
    }
}
