package com.bragi.bragi.rest;


import com.bragi.bragi.metrics.ServiceMetrics;
import com.bragi.bragi.metrics.WebFluxMetrics;
import com.bragi.bragi.rest.dto.Artist;
import com.bragi.bragi.rest.dto.Song;
import com.bragi.bragi.service.SongService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("songs")
@RequiredArgsConstructor
@Slf4j
public class SongController {

    private final SongService songService;
    private final ServiceMetrics serviceMetrics;

    @GetMapping("/page")
    public Mono<List<Song>> getSongs(@RequestParam("offset") int offset,
                                         @RequestParam("size") int size,
                                         @RequestParam("sort") String sort){
        return Flux
                .fromIterable(songService.findAllSongs(offset, size, sort))
                .map(Song::convertToDto)
                .collectList()
                .elapsed()
                .doOnNext(s-> WebFluxMetrics.recordMetric(serviceMetrics, "get_songs", s.getT1()))
                .flatMap(tuple-> Mono.just(tuple.getT2()))
                .doOnSubscribe(next->{
                    log.info("Received request to get songs");
                });

    }

    @GetMapping("/{id}")
    public Mono<Song> getSong(@PathVariable("id") String id){
        return Mono.just(songService.getSong(UUID.fromString(id)))
                .map(Song::convertToDto)
                .elapsed()
                .doOnNext(s-> WebFluxMetrics.recordMetric(serviceMetrics, "get_song", s.getT1()))
                .flatMap(tuple-> Mono.just(tuple.getT2()))
                .doOnSubscribe(next->{
                    log.info("Received request to get song");
                });
    }

    @PostMapping
    public Mono<Song> createSong(@RequestBody Song song, @RequestParam("file") MultipartFile file){
        return Mono.just(songService.store(song, file))
                .map(Song::convertToDto)
                .elapsed()
                .doOnNext(s-> WebFluxMetrics.recordMetric(serviceMetrics, "create_song",
                        s.getT1())).flatMap(tuple->Mono.just(tuple.getT2()))
                .doOnSubscribe(next->{
                    log.info("Received request to create song");
                });
    }

    @GetMapping("/download")
    public Mono<byte[]> downloadFile(@PathVariable("id") String id){
        return Mono.just(songService.getSong(UUID.fromString(id)))
                .map(song-> song.getSongContent().getContent())
                .elapsed()
                .doOnNext(s-> WebFluxMetrics.recordMetric(serviceMetrics, "download_song", s.getT1()))
                .flatMap(tuple-> Mono.just(tuple.getT2()))
                .doOnSubscribe(next->{
                    log.info("Received request to download song");
                });
    }

    @DeleteMapping
    public Mono<Void> deleteSong(@PathVariable("id") String id){
        return Mono.just(id)
                .map(next->{
                    songService.deleteSong(id);
                    return id;
                })
                .elapsed()
                .doOnNext(s-> {
                    WebFluxMetrics.recordMetric(serviceMetrics, "delete_song", s.getT1());
                })
                .then();
    }


}
