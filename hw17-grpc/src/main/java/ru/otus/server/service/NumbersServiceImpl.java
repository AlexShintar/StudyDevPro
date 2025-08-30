package ru.otus.server.service;

import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import ru.otus.proto.NumberValue;
import ru.otus.proto.NumbersServiceGrpc;
import ru.otus.proto.RangeRequest;

public final class NumbersServiceImpl extends NumbersServiceGrpc.NumbersServiceImplBase {

    @Override
    public void generate(RangeRequest request, StreamObserver<NumberValue> responseObserver) {
        final int start = request.getFirstValue() + 1;

        final int end = request.getLastValue();

        final ServerCallStreamObserver<NumberValue> serverObs =
                (ServerCallStreamObserver<NumberValue>) responseObserver;

        final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "numbers-generator");
            t.setDaemon(true);
            return t;
        });

        final AtomicInteger next = new AtomicInteger(start);

        final Runnable emit = () -> {
            int v = next.getAndIncrement();
            if (v <= end) {
                responseObserver.onNext(NumberValue.newBuilder().setValue(v).build());
                if (v == end) {
                    responseObserver.onCompleted();
                    scheduler.shutdown();
                }
            }
        };

        final ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(emit, 2, 2, TimeUnit.SECONDS);

        serverObs.setOnCancelHandler(() -> {
            future.cancel(true);
            scheduler.shutdownNow();
        });
    }
}
