package com.bragi.bragi.rest;

import com.bragi.bragi.metrics.ServiceMetrics;
import com.bragi.bragi.metrics.WebFluxMetrics;
import com.bragi.bragi.rest.dto.Artist;
import com.bragi.bragi.service.ArtistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.List;

//todo add caching
@RestController
@RequestMapping("artists")
@RequiredArgsConstructor
@Slf4j
public class ArtistController {

    private final ArtistService artistService;
    private final ServiceMetrics serviceMetrics;

    @GetMapping("/page")
    public Mono<List<Artist>> getArtists(@RequestParam("offset") int offset,
                                         @RequestParam("size") int size,
                                         @RequestParam("sort") String sort){
        return Flux
                .fromIterable(artistService.findAllArtists(offset, size, sort))
                .map(Artist::convertToDto)
                .collectList()
                .elapsed()
                .doOnNext(s-> WebFluxMetrics.recordMetric(serviceMetrics, "get_artists", s.getT1()))
                .flatMap(tuple-> Mono.just(tuple.getT2()))
                .doOnSubscribe(next->{
                    log.info("Received request to get artists");
                });

    }

    @GetMapping("/{id}")
    public Mono<Artist> getArtist(@PathVariable("id") String id){
        return Mono.just(artistService.getArtist(id))
                .map(Artist::convertToDto)
                .elapsed()
                .doOnNext(s-> WebFluxMetrics.recordMetric(serviceMetrics, "get_artist", s.getT1()))
                .flatMap(tuple-> Mono.just(tuple.getT2()))
                .doOnSubscribe(next->{
                    log.info("Received request to get artist");
                });

    }

    @PostMapping
    public Mono<Artist> createArtist(@RequestBody Artist artist){
        return Mono.just(artistService.store(artist))
                .map(Artist::convertToDto)
                .elapsed()
                .doOnNext(s-> WebFluxMetrics.recordMetric(serviceMetrics, "create_artist",
                        s.getT1())).flatMap(tuple->Mono.just(tuple.getT2()))
                .doOnSubscribe(next->{
                    log.info("Received request to create artist");
                });
    }

    @DeleteMapping
    public Mono<Void> deleteArtist(@PathVariable("id") String id){
        return Mono.just(id)
                .map(next->{
                    artistService.deleteArtist(id);
                    return id;
                })
                .elapsed()
                .doOnNext(s-> {
                    WebFluxMetrics.recordMetric(serviceMetrics, "delete_artist", s.getT1());
                })
                .then();
    }



}
