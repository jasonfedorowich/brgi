package com.bragi.bragi.service;

import com.bragi.bragi.model.Album;
import com.bragi.bragi.model.Artist;
import com.bragi.bragi.model.Song;
import com.bragi.bragi.model.SongContent;
import com.bragi.bragi.repository.DataAccessService;
import com.bragi.bragi.repository.SongRepository;
import com.bragi.bragi.service.config.RetryConfig;
import com.bragi.bragi.service.utils.GrpcObjectMapper;
import com.bragi.bragi.service.utils.RetryUtils;
import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Set;
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


}
