package com.bragi.bragi.server;

import com.google.common.util.concurrent.UncaughtExceptionHandlers;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
@Slf4j
public class MusicServer implements CommandLineRunner {

    private Server server;
    private final GrpcArtistService grpcArtistService;

    @Override
    public void run(String... args) throws Exception {

        server = ServerBuilder
                .forPort(9999)
                .addService(grpcArtistService)
                .build();
        server.start()
                .awaitTermination();
    }

    @PreDestroy
    public void teardown(){
        if(server != null){
            server.shutdown();
            if(!server.isShutdown())
                server.shutdownNow();
        }


    }

    private Executor getServerExecuter(int numOfThreads){
        return new ForkJoinPool(numOfThreads,
                new ForkJoinPool.ForkJoinWorkerThreadFactory() {
                    final AtomicInteger integer = new AtomicInteger();
                    @Override
                    public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
                        var thread = ForkJoinPool.defaultForkJoinWorkerThreadFactory
                                .newThread(pool);
                        thread.setDaemon(true);
                        thread.setName("grpc-server");
                        return thread;
                    }
                } , UncaughtExceptionHandlers.systemExit(), true);
    }
}
