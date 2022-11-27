package com.bragi.bragi.repository;

import com.bragi.bragi.model.Album;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AlbumRepository extends CrudRepository<Album, Long> {

    Optional<Album> findByTitle(String title);

}
