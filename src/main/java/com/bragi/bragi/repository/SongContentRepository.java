package com.bragi.bragi.repository;

import com.bragi.bragi.model.SongContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface SongContentRepository extends JpaRepository<SongContent, Long> {

}
