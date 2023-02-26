package com.bragi.bragi.repository;

import com.bragi.bragi.model.Album;
import com.bragi.bragi.model.Artist;
import com.bragi.bragi.model.Song;
import com.bragi.bragi.model.SongContent;
import com.bragi.bragi.service.ArtistService;
import com.bragi.bragi.service.config.RetryConfig;
import com.bragi.bragi.service.utils.RetryUtils;
import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataAccessService {

    private final RetryConfig retryConfig;

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final SongRepository songRepository;

    //region artist
    public Artist saveArtist(Artist artist){
        return RetryUtils.getResultFromRetry(()-> artistRepository.save(artist),
                "Starting a save operation on artists",
                "Retrying save operation on artists",
                retryConfig.getMaxAttempts(),
                retryConfig.getMaxWaitBetweenMillis(),
                retryConfig.getMinWaitBetweenMillis());
    }

    public Artist getArtistById(long id){
        return RetryUtils.getResultFromRetry(()->{
                    return artistRepository.findById(id);
                }, "Starting a get artist by id operation on artists",
                "Retrying get artist by id operation on artists",
                retryConfig.getMaxAttempts(),
                retryConfig.getMaxWaitBetweenMillis(),
                retryConfig.getMinWaitBetweenMillis()).orElseThrow();
    }

    public void deleteArtistById(long id){
        RetryUtils.getResultFromRetry(()->{
                    artistRepository.deleteById(id);
                    return null;
                }, "Starting delete operation: {}",
                "Running delete by id operation number: {}", retryConfig.getMaxAttempts(),
                retryConfig.getMaxWaitBetweenMillis(),
                retryConfig.getMinWaitBetweenMillis());
    }

    public Set<Album> getAllAlbumsFromArtistId(long id) {
        return RetryUtils.getResultFromRetry(()-> artistRepository.findById(id)
                        .orElseThrow().getAlbums()
                , "Starting get by id operation: {}",
                "Running get id operation number: {}", retryConfig.getMaxAttempts(),
                retryConfig.getMaxWaitBetweenMillis(),
                retryConfig.getMinWaitBetweenMillis());
    }

    public Set<Song> getAllSongsFromArtistId(long id){
        return RetryUtils.getResultFromRetry(()-> artistRepository.findById(id)
                        .orElseThrow()
                        .getSongs(), "Starting get by id operation: {}",
                "Running get id operation number: {}", retryConfig.getMaxAttempts(),
                retryConfig.getMaxWaitBetweenMillis(),
                retryConfig.getMinWaitBetweenMillis());
    }

    public Artist getArtistByName(String name){
        return RetryUtils.getResultFromRetry(()-> artistRepository.findByName(name)
                        .orElseThrow(), "Starting get by name operation: {}",
                "Running get name operation number: {}", retryConfig.getMaxAttempts(),
                retryConfig.getMaxWaitBetweenMillis(),
                retryConfig.getMinWaitBetweenMillis());
    }

    public Artist getArtistByExternalId(UUID externalId){
        return RetryUtils.getResultFromRetry(()-> artistRepository.findByExternalId(externalId)
                        .orElseThrow(), "Starting get by externalId operation: {}",
                "Running get externalId operation number: {}", retryConfig.getMaxAttempts(),
                retryConfig.getMaxWaitBetweenMillis(),
                retryConfig.getMinWaitBetweenMillis());
    }

    //endregion

    //region album
    public Album saveAlbum(Album album){
        return RetryUtils.getResultFromRetry(()-> albumRepository.save(album), "Starting save operation: {}",
                "Running save operation number: {}", retryConfig.getMaxAttempts(),
                retryConfig.getMaxWaitBetweenMillis(),
                retryConfig.getMinWaitBetweenMillis());
    }

    public Album getAlbumById(long id){
        return RetryUtils.getResultFromRetry(()-> albumRepository.findById(id), "Starting get by id operation: {}",
                        "Running get id operation number: {}", retryConfig.getMaxAttempts(),
                        retryConfig.getMaxWaitBetweenMillis(),
                        retryConfig.getMinWaitBetweenMillis())
                .orElseThrow();
    }

    public void deleteAlbumById(long id){
        RetryUtils.getResultFromRetry(()->{
                    albumRepository.deleteById(id);
                    return null;
                }, "Starting delete operation: {}",
                "Running delete by id operation number: {}", retryConfig.getMaxAttempts(),
                retryConfig.getMaxWaitBetweenMillis(),
                retryConfig.getMinWaitBetweenMillis());
    }

    public Set<Artist> getAllArtistsFromAlbumId(long id) {
        return RetryUtils.getResultFromRetry(()-> albumRepository.findById(id)
                        .orElseThrow()
                        .getArtists(), "Starting get by id operation: {}",
                "Running get id operation number: {}", retryConfig.getMaxAttempts(),
                retryConfig.getMaxWaitBetweenMillis(),
                retryConfig.getMinWaitBetweenMillis());
    }

    public Set<Song> getAllSongsFromAlbumId(long id){
        return RetryUtils.getResultFromRetry(()-> albumRepository.findById(id)
                        .orElseThrow()
                        .getSongs(), "Starting get by id operation: {}",
                "Running get id operation number: {}", retryConfig.getMaxAttempts(),
                retryConfig.getMaxWaitBetweenMillis(),
                retryConfig.getMinWaitBetweenMillis());
    }

    public Album getAlbumByName(String name){
        return RetryUtils.getResultFromRetry(()-> albumRepository.findByTitle(name)
                        .orElseThrow(), "Starting get by name operation: {}",
                "Running get name operation number: {}", retryConfig.getMaxAttempts(),
                retryConfig.getMaxWaitBetweenMillis(),
                retryConfig.getMinWaitBetweenMillis());
    }

    public Album getAlbumByExternalId(UUID externalId){
        return RetryUtils.getResultFromRetry(()-> albumRepository.findByExternalId(externalId)
                        .orElseThrow(), "Starting get by externalId operation: {}",
                "Running get externalId operation number: {}", retryConfig.getMaxAttempts(),
                retryConfig.getMaxWaitBetweenMillis(),
                retryConfig.getMinWaitBetweenMillis());
    }
    //endregion

    //region song
    public Song saveSong(Song song){
        return RetryUtils.getResultFromRetry(()-> songRepository.save(song),
                "Starting a save operation on artists",
                "Retrying save operation on artists",
                retryConfig.getMaxAttempts(),
                retryConfig.getMaxWaitBetweenMillis(),
                retryConfig.getMinWaitBetweenMillis());
    }

    public Song getSongById(long id){
        return RetryUtils.getResultFromRetry(()-> songRepository.findById(id), "Starting a get song by id operation on artists",
                "Retrying get song by id operation on artists",
                retryConfig.getMaxAttempts(),
                retryConfig.getMaxWaitBetweenMillis(),
                retryConfig.getMinWaitBetweenMillis()).orElseThrow();
    }

    public void deleteSongById(long id){
        RetryUtils.getResultFromRetry(()->{
                    songRepository.deleteById(id);
                    return null;
                }, "Starting delete operation: {}",
                "Running delete by id operation number: {}", retryConfig.getMaxAttempts(),
                retryConfig.getMaxWaitBetweenMillis(),
                retryConfig.getMinWaitBetweenMillis());
    }

    public Album getAlbumBySongId(long id) {
        return RetryUtils.getResultFromRetry(()-> songRepository.findById(id)
                        .orElseThrow().getAlbum()
                , "Starting get by id operation: {}",
                "Running get id operation number: {}", retryConfig.getMaxAttempts(),
                retryConfig.getMaxWaitBetweenMillis(),
                retryConfig.getMinWaitBetweenMillis());
    }

    public Set<Artist> getAllArtistsFromSongId(long id){
        return RetryUtils.getResultFromRetry(()-> songRepository.findById(id)
                        .orElseThrow()
                        .getArtists(), "Starting get by id operation: {}",
                "Running get id operation number: {}", retryConfig.getMaxAttempts(),
                retryConfig.getMaxWaitBetweenMillis(),
                retryConfig.getMinWaitBetweenMillis());
    }

    public Song getSongByName(String name){
        return RetryUtils.getResultFromRetry(()-> songRepository.findByTitle(name)
                        .orElseThrow(), "Starting get by title operation: {}",
                "Running get title operation number: {}", retryConfig.getMaxAttempts(),
                retryConfig.getMaxWaitBetweenMillis(),
                retryConfig.getMinWaitBetweenMillis());
    }

    public Song getSongByExternalId(UUID externalId){
        return RetryUtils.getResultFromRetry(()-> songRepository.findByExternalId(externalId)
                        .orElseThrow(), "Starting get by externalId operation: {}",
                "Running get externalId operation number: {}", retryConfig.getMaxAttempts(),
                retryConfig.getMaxWaitBetweenMillis(),
                retryConfig.getMinWaitBetweenMillis());
    }


    //endregion


    //region load song content

    //todo need to write unit tests
    public SongContent getSongContentByExternalId(UUID externalId){
        return RetryUtils.getResultFromRetry(()-> songRepository.findByExternalId(externalId)
                        .orElseThrow(), "Starting get by externalId operation: {}",
                "Running get externalId operation number: {}", retryConfig.getMaxAttempts(),
                retryConfig.getMaxWaitBetweenMillis(),
                retryConfig.getMinWaitBetweenMillis())
                .getSongContent();
    }

    public SongContent getSongContentFromSong(Song song){
        return song.getSongContent();
    }

    public brgi.grpc.Song getSongContentAndConvert(Song song){
        var songContent = song.getSongContent();

        var artistIds = song.getArtists()
                .stream()
                .map(artist -> artist.getExternalId().toString()).collect(Collectors.toSet());

        return brgi.grpc.Song
                .newBuilder()
                .setContent(ByteString.copyFrom(songContent.getContent()))
                .setDuration(song.getDuration())
                .setReleaseDate(com.google.protobuf.Timestamp.newBuilder()
                        .setNanos(song.getDateReleased().getNanos())
                        .setSeconds(song.getDateReleased().getSeconds()))
                .setTitle(song.getTitle())
                .setAlbumId(song.getAlbum().getExternalId().toString())
                .addAllArtistId(artistIds)
                .build();
    }

    //endregion
}
