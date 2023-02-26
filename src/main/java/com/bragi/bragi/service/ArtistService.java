package com.bragi.bragi.service;

import com.bragi.bragi.model.Album;
import com.bragi.bragi.model.Artist;
import com.bragi.bragi.model.Song;
import com.bragi.bragi.repository.ArtistRepository;
import com.bragi.bragi.repository.DataAccessService;
import com.bragi.bragi.retry.Retry;
import com.bragi.bragi.service.config.RetryConfig;
import com.bragi.bragi.service.utils.GrpcObjectMapper;
import com.bragi.bragi.service.utils.RetryUtils;
import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArtistService {

    private final DataAccessService dataAccessService;

    public brgi.grpc.AddArtistResponse store(brgi.grpc.AddArtistRequest request){
        var artist = Artist.builder()
                .name(request.getArtist().getName())
                .timeStarted(new Timestamp(request.getArtist().getTimeFormed().getSeconds() * 1_000 +
                        request.getArtist().getTimeFormed().getNanos() / 1_000_000))
                .build();

        var artistFromDb = dataAccessService.saveArtist(artist);

        return brgi.grpc.AddArtistResponse.newBuilder()
                .setArtist(request.getArtist())
                .setArtistId(artistFromDb.getExternalId().toString())
                .build();
    }

    public brgi.grpc.GetArtistResponse getArtist(brgi.grpc.GetArtistRequest request){
        var artistId = UUID.fromString(request.getArtistId());
        var artistFromDb = dataAccessService.getArtistByExternalId(artistId);

        var artist = GrpcObjectMapper.mapArtist(artistFromDb);

        return brgi.grpc.GetArtistResponse.newBuilder()
                        .setArtist(artist)
                .build();

    }

    public Set<Song> getSongs(UUID externalId){
        var albumFromDb = dataAccessService.getArtistByExternalId(externalId);
        return albumFromDb.getSongs();
    }


    public brgi.grpc.GetAllAlbumsResponse getAllAlbums(brgi.grpc.GetAllAlbumsRequest request){
        var artist = dataAccessService.getArtistByExternalId(UUID.fromString(request.getArtistId()));

        var albums = artist.getAlbums()
                .stream()
                .map(GrpcObjectMapper::mapAlbum)
                .collect(Collectors.toSet());

        return brgi.grpc.GetAllAlbumsResponse.newBuilder()
                .addAllAlbum(albums)
                .build();

    }

    public brgi.grpc.GetAllSongsResponse getAllSongs(brgi.grpc.GetAllSongsRequest request){
        if(!request.hasArtistId())
            throw new RuntimeException("Artist id needs to be set");

        var artist = dataAccessService.getArtistByExternalId(UUID.fromString(request.getArtistId()));

        var songs = artist.getSongs()
                .stream()
                .map(GrpcObjectMapper::mapSong)
                .collect(Collectors.toSet());

        return brgi.grpc.GetAllSongsResponse.newBuilder()
                .addAllSong(songs)
                .build();
    }

    public brgi.grpc.DeleteArtistResponse deleteArtist(brgi.grpc.DeleteArtistRequest request){
        Artist artist;
        try{
            artist = dataAccessService.getArtistByExternalId(UUID.fromString(request.getArtistId()));
        }catch(NoSuchElementException e){
            throw new RuntimeException(String.format("No artist with external id: %s exists", request.getArtistId()));
        }
        dataAccessService.deleteArtistById(artist.getId());
        return brgi.grpc.DeleteArtistResponse.getDefaultInstance();
    }

}
