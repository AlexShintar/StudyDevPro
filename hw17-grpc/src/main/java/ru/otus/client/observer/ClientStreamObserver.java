package ru.otus.client.observer;

import io.grpc.stub.StreamObserver;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.client.Latest;
import ru.otus.proto.NumberValue;

public final class ClientStreamObserver implements StreamObserver<NumberValue> {
    private static final Logger log = LoggerFactory.getLogger(ClientStreamObserver.class);

    private final AtomicReference<Latest> latestRef;

    private final CountDownLatch done;

    public ClientStreamObserver(AtomicReference<Latest> latestRef, CountDownLatch done) {
        this.latestRef = latestRef;
        this.done = done;
    }

    @Override
    public void onNext(NumberValue value) {
        Latest prev = latestRef.get();
        latestRef.set(new Latest(value.getValue(), prev.seq + 1));
        log.info("new value:{}", value.getValue());
    }

    @Override
    public void onError(Throwable t) {
        log.error("stream error", t);
        done.countDown();
    }

    @Override
    public void onCompleted() {
        log.info("request completed");
        done.countDown();
    }
}
