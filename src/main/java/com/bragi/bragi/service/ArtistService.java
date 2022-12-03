package com.bragi.bragi.service;

import com.bragi.bragi.model.Album;
import com.bragi.bragi.model.Artist;
import com.bragi.bragi.model.Song;
import com.bragi.bragi.repository.ArtistRepository;
import com.bragi.bragi.retry.Retry;
import com.bragi.bragi.service.config.RetryConfig;
import com.bragi.bragi.service.utils.RetryUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.Callable;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArtistService {

    private final ArtistRepository artistRepository;

    private final RetryConfig retryConfig;

    public Artist store(Artist artist){
        return RetryUtils.getResultFromRetry(()-> artistRepository.save(artist),
                "Starting a save operation on artists",
                "Retrying save operation on artists",
                retryConfig.getMaxAttempts(),
                retryConfig.getMaxWaitBetweenMillis(),
                retryConfig.getMinWaitBetweenMillis());
    }

    public Artist getById(long id){
        return RetryUtils.getResultFromRetry(()->{
            return artistRepository.findById(id);
        }, "Starting a get artist by id operation on artists",
                "Retrying get artist by id operation on artists",
                retryConfig.getMaxAttempts(),
                retryConfig.getMaxWaitBetweenMillis(),
                retryConfig.getMinWaitBetweenMillis()).orElseThrow();
    }

    public void deleteById(long id){
        RetryUtils.getResultFromRetry(()->{
                    artistRepository.deleteById(id);
                    return null;
                }, "Starting delete operation: {}",
                "Running delete by id operation number: {}", retryConfig.getMaxAttempts(),
                retryConfig.getMaxWaitBetweenMillis(),
                retryConfig.getMinWaitBetweenMillis());
    }

    public Set<Album> getAllAlbums(long id) {
        return RetryUtils.getResultFromRetry(()-> artistRepository.findById(id)
                        .orElseThrow().getAlbums()
                , "Starting get by id operation: {}",
                "Running get id operation number: {}", retryConfig.getMaxAttempts(),
                retryConfig.getMaxWaitBetweenMillis(),
                retryConfig.getMinWaitBetweenMillis());
    }

    public Set<Song> getAllSongs(long id){
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


}
