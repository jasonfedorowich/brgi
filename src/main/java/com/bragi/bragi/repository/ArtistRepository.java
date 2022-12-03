package com.bragi.bragi.repository;

import com.bragi.bragi.model.Artist;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ArtistRepository extends CrudRepository<Artist, Long> {
    Optional<Artist> findByName(String name);
}
