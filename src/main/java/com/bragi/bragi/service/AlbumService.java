package com.bragi.bragi.service;

import com.bragi.bragi.model.Album;
import com.bragi.bragi.model.Artist;
import com.bragi.bragi.model.Song;
import com.bragi.bragi.repository.AlbumRepository;
import com.bragi.bragi.retry.Retry;
import com.bragi.bragi.service.config.RetryConfig;
import com.bragi.bragi.service.utils.RetryUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final RetryConfig retryConfig;

    public Album store(Album album){
        return RetryUtils.getResultFromRetry(()-> albumRepository.save(album), "Starting save operation: {}",
                "Running save operation number: {}", retryConfig.getMaxAttempts(),
                retryConfig.getMaxWaitBetweenMillis(),
                retryConfig.getMinWaitBetweenMillis());
    }

    public Album getById(long id){
        return RetryUtils.getResultFromRetry(()-> albumRepository.findById(id), "Starting get by id operation: {}",
                "Running get id operation number: {}", retryConfig.getMaxAttempts(),
                        retryConfig.getMaxWaitBetweenMillis(),
                        retryConfig.getMinWaitBetweenMillis())
                .orElseThrow();
    }

    public void deleteById(long id){
        RetryUtils.getResultFromRetry(()->{
            albumRepository.deleteById(id);
            return null;
        }, "Starting delete operation: {}",
                "Running delete by id operation number: {}", retryConfig.getMaxAttempts(),
                retryConfig.getMaxWaitBetweenMillis(),
                retryConfig.getMinWaitBetweenMillis());
    }

    public Set<Artist> getAllArtists(long id) {
       return RetryUtils.getResultFromRetry(()-> albumRepository.findById(id)
               .orElseThrow()
               .getArtists(), "Starting get by id operation: {}",
               "Running get id operation number: {}", retryConfig.getMaxAttempts(),
               retryConfig.getMaxWaitBetweenMillis(),
               retryConfig.getMinWaitBetweenMillis());
    }

    public Set<Song> getAllSongs(long id){
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
}
