package ru.otus.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.client.observer.ClientStreamObserver;
import ru.otus.proto.NumbersServiceGrpc;
import ru.otus.proto.RangeRequest;

public final class NumbersClient {
    private static final Logger log = LoggerFactory.getLogger(NumbersClient.class);

    private static final int SERVER_PORT = 8191;

    private static final String SERVER_HOST = "localhost";

    private static final int FIRST_VALUE = 0;

    private static final int LAST_VALUE = 30;

    private static final int CYCLE_END = 50;

    private static final Duration SLEEP_TIME = Duration.of(1, ChronoUnit.SECONDS);

    public static void main(String[] args) throws Exception {
        log.info("numbers Client is starting...");

        ManagedChannel channel = ManagedChannelBuilder.forAddress(SERVER_HOST, SERVER_PORT)
                .usePlaintext()
                .build();

        NumbersServiceGrpc.NumbersServiceStub stub = NumbersServiceGrpc.newStub(channel);

        AtomicReference<Latest> latestRef = new AtomicReference<>(new Latest(0, 0));
        CountDownLatch done = new CountDownLatch(1);

        RangeRequest request = RangeRequest.newBuilder()
                .setFirstValue(FIRST_VALUE)
                .setLastValue(LAST_VALUE)
                .build();
        stub.generate(request, new ClientStreamObserver(latestRef, done));

        int currentValue = 0;
        int usedSeq = 0;

        for (int i = 0; i <= CYCLE_END; i++) {
            Latest latest = latestRef.get();
            if (latest.seq > usedSeq) {
                currentValue = currentValue + latest.value + 1;
                usedSeq = latest.seq;
            } else {
                currentValue = currentValue + 1;
            }
            log.info("currentValue:{}", currentValue);

            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }

        done.await(70, TimeUnit.SECONDS);

        channel.shutdown();
        if (!channel.awaitTermination(3, TimeUnit.SECONDS)) {
            channel.shutdownNow();
        }
    }
}
