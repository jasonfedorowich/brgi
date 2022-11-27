package com.bragi.bragi.repository;

import com.bragi.bragi.model.Artist;
import org.springframework.data.repository.CrudRepository;

public interface ArtistRepository extends CrudRepository<Artist, Long> {
}
