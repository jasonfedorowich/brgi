package com.bragi.bragi.service;

import com.bragi.bragi.model.Artist;
import com.bragi.bragi.model.Song;
import com.bragi.bragi.repository.DataAccessService;
import com.bragi.bragi.service.utils.GrpcObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
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
        var artist = dataAccessService.getArtistByExternalId(externalId);
        return artist.getSongs();
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

    public List<Artist> findAllArtists(int offset, int size, String sort) {
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
        Page<Artist> artistPage = dataAccessService.findAllArtists(pageable);
        return artistPage.getContent();

    }


    public Artist getArtist(String id) {
        return dataAccessService.getArtistByExternalId(UUID.fromString(id));
    }

    public Artist store(com.bragi.bragi.rest.dto.Artist artist) {
        return dataAccessService.saveArtist(Artist.builder()
                        .timeStarted(artist.getTimeStarted())
                        .name(artist.getName())
                .build());
    }

    public void deleteArtist(String id){
        Artist artist;
        try{
            artist = dataAccessService.getArtistByExternalId(UUID.fromString(id));
        }catch (NoSuchElementException e){
            throw new RuntimeException("No artist found to delete");
        }
        dataAccessService.deleteArtistById(artist.getId());
    }
}
