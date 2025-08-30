package ru.otus.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.server.service.NumbersServiceImpl;

public final class NumbersServer {
    private static final Logger log = LoggerFactory.getLogger(NumbersServer.class);

    private static final int SERVER_PORT = 8191;

    public static void main(String[] args) throws Exception {
        log.info("numbers Server is starting...");
        Server server = ServerBuilder.forPort(SERVER_PORT)
                .addService(new NumbersServiceImpl())
                .build()
                .start();

        log.info("numbers Server started on port {}", SERVER_PORT);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("numbers Server is shutting down...");
            server.shutdown();
        }));

        server.awaitTermination();
    }
}
