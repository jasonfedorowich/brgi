package com.bragi.bragi.rest;


import com.bragi.bragi.metrics.ServiceMetrics;
import com.bragi.bragi.metrics.WebFluxMetrics;
import com.bragi.bragi.rest.dto.Album;
import com.bragi.bragi.rest.dto.Artist;
import com.bragi.bragi.service.AlbumService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("albums")
@RequiredArgsConstructor
@Slf4j
public class AlbumController {

    private final AlbumService albumService;
    private final ServiceMetrics serviceMetrics;

    @GetMapping("/page")
    public Mono<List<Album>> getAlbums(@RequestParam("offset") int offset,
                                         @RequestParam("size") int size,
                                         @RequestParam("sort") String sort){
        return Flux
                .fromIterable(albumService.findAllAlbums(offset, size, sort))
                .map(Album::convertToDto)
                .collectList()
                .elapsed()
                .doOnNext(s-> WebFluxMetrics.recordMetric(serviceMetrics, "get_albums", s.getT1()))
                .flatMap(tuple-> Mono.just(tuple.getT2()))
                .doOnSubscribe(next->{
                    log.info("Received request to get albums");
                });

    }

    @GetMapping("/{id}")
    public Mono<Album> getAlbum(@PathVariable("id") String id){
        return Mono.just(albumService.getAlbum(id))
                .map(Album::convertToDto)
                .elapsed()
                .doOnNext(s-> WebFluxMetrics.recordMetric(serviceMetrics, "get_album", s.getT1()))
                .flatMap(tuple-> Mono.just(tuple.getT2()))
                .doOnSubscribe(next->{
                    log.info("Received request to get album");
                });

    }

    @PostMapping
    public Mono<Album> createAlbum(@RequestBody Album album){
        return Mono.just(albumService.store(album))
                .map(Album::convertToDto)
                .elapsed()
                .doOnNext(s-> WebFluxMetrics.recordMetric(serviceMetrics, "create_album",
                        s.getT1())).flatMap(tuple->Mono.just(tuple.getT2()))
                .doOnSubscribe(next->{
                    log.info("Received request to create album");
                });
    }

    @DeleteMapping
    public Mono<Void> deleteAlbum(@PathVariable("id") String id){
        return Mono.just(id)
                .map(next->{
                    albumService.deleteAlbum(id);
                    return id;
                })
                .elapsed()
                .doOnNext(s-> {
                    WebFluxMetrics.recordMetric(serviceMetrics, "delete_album", s.getT1());
                })
                .then();
    }



}
