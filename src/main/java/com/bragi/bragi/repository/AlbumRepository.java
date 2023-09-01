package com.bragi.bragi.repository;

import com.bragi.bragi.model.Album;
import com.bragi.bragi.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface AlbumRepository extends JpaRepository<Album, Long> {

    Optional<Album> findByTitle(String title);

    Optional<Album> findByExternalId(UUID externalId);

}
