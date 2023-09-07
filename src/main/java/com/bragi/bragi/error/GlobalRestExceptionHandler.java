package com.bragi.bragi.error;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

@Component
public class GlobalRestExceptionHandler implements WebExceptionHandler {

    //todo
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {


        return null;
    }
}
