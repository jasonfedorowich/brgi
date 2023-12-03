package com.bragi.bragi.error;

import org.hibernate.StaleObjectStateException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.test.StepVerifier;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalRestExceptionHandlerTest {

    private GlobalRestExceptionHandler globalRestExceptionHandler;

    @BeforeEach
    void setUp() {
        globalRestExceptionHandler = new GlobalRestExceptionHandler();
    }

    @Test
    void when_handleExceptionNotFound_returnsExpected(){
        ServerWebExchange serverWebExchange = mock(ServerWebExchange.class);
        ServerHttpResponse response = mock(ServerHttpResponse.class);

        when(serverWebExchange.getResponse()).thenReturn(response);

        StepVerifier.create(globalRestExceptionHandler.handle(serverWebExchange, new NoSuchElementException()))
                .verifyComplete();

        verify(response).setStatusCode(eq(HttpStatus.NOT_FOUND));
    }

    @Test
    void when_handleClientException_returnsExpected(){
        ServerWebExchange serverWebExchange = mock(ServerWebExchange.class);
        ServerHttpResponse response = mock(ServerHttpResponse.class);

        when(serverWebExchange.getResponse()).thenReturn(response);

        StepVerifier.create(globalRestExceptionHandler.handle(serverWebExchange, new ClientException()))
                .verifyComplete();

        verify(response).setStatusCode(eq(HttpStatus.BAD_REQUEST));
    }

    @Test
    void when_handleInternal_returnsExpected(){
        ServerWebExchange serverWebExchange = mock(ServerWebExchange.class);
        ServerHttpResponse response = mock(ServerHttpResponse.class);

        when(serverWebExchange.getResponse()).thenReturn(response);

        StepVerifier.create(globalRestExceptionHandler.handle(serverWebExchange, new ArrayIndexOutOfBoundsException()))
                .verifyComplete();

        verify(response).setStatusCode(eq(HttpStatus.INTERNAL_SERVER_ERROR));
    }


}