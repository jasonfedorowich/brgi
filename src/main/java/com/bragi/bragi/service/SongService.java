package com.bragi.bragi.service;

import com.bragi.bragi.model.Song;
import com.bragi.bragi.model.SongContent;
import com.bragi.bragi.repository.DataAccessService;
import com.bragi.bragi.service.utils.GrpcObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SongService {

    private final DataAccessService dataAccessService;

    public brgi.grpc.AddSongResponse store(brgi.grpc.AddSongRequest request){
        var song = request.getSong();
        var songBuilder = Song.builder()
                .songContent(SongContent.builder()
                        .content(song.getContent().toByteArray())
                        .build())
                .title(song.getTitle())
                .duration(song.getDuration())
                .dateReleased(new Timestamp(song.getReleaseDate().getSeconds() * 1_000 +
                        song.getReleaseDate().getNanos() / 1_000_000));

        var artists = song.getArtistIdList()
                .stream()
                .map(artistId->
                        dataAccessService.getArtistByExternalId(UUID.fromString(artistId)))
                .collect(Collectors.toSet());

        var album = dataAccessService.getAlbumByExternalId(UUID.fromString(song.getAlbumId()));

        var songToDb = songBuilder.album(album)
                .artists(artists)
                .build();

        var songFromDb = dataAccessService.saveSong(songToDb);
        song = song.toBuilder()
                .setSongId(songFromDb.getExternalId().toString())
                .build();

        return brgi.grpc.AddSongResponse.newBuilder()
                .setSong(song)
                .setSongId(songFromDb.getExternalId().toString())
                .build();
    }

    public brgi.grpc.GetSongResponse getSong(brgi.grpc.GetSongRequest request){
        var songFromDb = dataAccessService.getSongByExternalId(UUID.fromString(request.getSongId()));
        var song = GrpcObjectMapper.mapSong(songFromDb);

        return brgi.grpc.GetSongResponse
                .newBuilder()
                .setSong(song)
                .build();
    }

    public Song getSong(UUID externalId){
        return dataAccessService.getSongByExternalId(externalId);
    }

    public brgi.grpc.GetSongAlbumResponse getSongAlbum(brgi.grpc.GetSongAlbumRequest request){
        var songFromDb = dataAccessService.getSongByExternalId(UUID.fromString(request.getSongId()));
        var albumFromDb = songFromDb.getAlbum();
        var album = GrpcObjectMapper.mapAlbum(albumFromDb);


        return brgi.grpc.GetSongAlbumResponse.newBuilder()
                .setAlbum(album)
                .build();

    }

    public brgi.grpc.GetArtistsResponse getSongsArtists(brgi.grpc.GetArtistsRequest request){
        if(!request.hasSongId())
            throw new RuntimeException("Request must have song id");

        var songFromDb = dataAccessService.getSongByExternalId(UUID.fromString(request.getSongId()));

        var artists = songFromDb.getArtists()
                .stream()
                .map(GrpcObjectMapper::mapArtist)
                .toList();

        return brgi.grpc.GetArtistsResponse.newBuilder()
                .addAllArtist(artists)
                .build();

    }

    public brgi.grpc.DeleteSongResponse deleteSong(brgi.grpc.DeleteSongRequest request){
        var songFromDb = dataAccessService.getSongByExternalId(UUID.fromString(request.getSongId()));
        dataAccessService.deleteSongById(songFromDb.getId());
        return brgi.grpc.DeleteSongResponse.getDefaultInstance();
    }


    public List<Song> findAllSongs(int offset, int size, String sort) {
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
        Page<Song> songPage = dataAccessService.findAllSongs(pageable);
        return songPage.getContent();

    }

    public Song store(com.bragi.bragi.rest.dto.Song song, MultipartFile file) {
        try{
            var songModel = com.bragi.bragi.model.Song.builder()
                    .duration(song.getDuration())
                    .dateReleased(song.getDateReleased())
                    .externalId(song.getExternalId())
                    .id(song.getId())
                    .title(song.getTitle())
                    .artists(song.getArtists()
                            .stream()
                            .map(dataAccessService::getArtistByExternalId).collect(Collectors.toSet()))
                    .songContent(
                            SongContent.builder()
                                    .content(file.getBytes())
                                    .build()
                    )
                    .album(dataAccessService.getAlbumByExternalId(song.getAlbum()))
                    .build();
            return dataAccessService.saveSong(songModel);
        }catch (IOException e){
            throw new RuntimeException("Cannot access file: " + file.getName());
        }

    }

    public void deleteSong(String id) {
        Song song;
        try{
           song = dataAccessService.getSongByExternalId(UUID.fromString(id));
        }catch (NoSuchElementException e){
            throw new RuntimeException("No such song exists");
        }
        dataAccessService.deleteSongById(song.getId());
    }
}
