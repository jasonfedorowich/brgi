package com.bragi.bragi.bootstrap;

import com.bragi.bragi.model.Album;
import com.bragi.bragi.model.Artist;
import com.bragi.bragi.repository.AlbumRepository;
import com.bragi.bragi.repository.ArtistRepository;
import com.bragi.bragi.repository.SongRepository;
import com.bragi.bragi.service.AlbumService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class Bootstrap implements CommandLineRunner {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final SongRepository songRepository;

    @Override
    public void run(String... args) throws Exception {
        var artist1 = Artist.builder().build();
        artist1 = artistRepository.save(artist1);
        log.info("Artist: {}",  artist1);
        var album = Album.builder().build();
        album.getArtists().add(artist1);
        album = albumRepository.save(album);
        artist1.getAlbums().add(album);
        artistRepository.save(artist1);
        log.info("Album: {}", album);
        var albumFromDb = albumRepository.findById(album.getId());
        var artists = albumFromDb.orElseThrow().getArtists();
        log.info("is true: {}", artists.contains(artist1));

    }
}
