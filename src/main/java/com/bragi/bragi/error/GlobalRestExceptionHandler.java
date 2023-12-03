package com.bragi.bragi.error;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@Component
public class GlobalRestExceptionHandler implements WebExceptionHandler {

    //todo expand errors
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if(ex instanceof NoSuchElementException){
            exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
            exchange.getResponse().setComplete();
        }else if(ex instanceof ClientException){
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            exchange.getResponse().setComplete();
        }else{
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            exchange.getResponse().setComplete();
        }

        return Mono.empty();
    }
}
