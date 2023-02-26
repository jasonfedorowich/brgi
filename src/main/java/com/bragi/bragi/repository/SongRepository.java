package com.bragi.bragi.repository;

import com.bragi.bragi.model.Song;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface SongRepository extends CrudRepository<Song, Long> {
    Optional<Song> findByTitle(String name);
    Optional<Song> findByExternalId(UUID externalId);
}
