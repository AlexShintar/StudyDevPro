package ru.otus.listener.homework;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.listener.Listener;
import ru.otus.model.Message;

public final class HistoryListener implements Listener, HistoryReader {

    private static final Logger logger = LoggerFactory.getLogger(HistoryListener.class);
    private final Map<Long, Message> messages = new HashMap<>();

    @Override
    public void onUpdated(Message msg) {
        Message previous = messages.get(msg.getId());
        if (previous == null || !previous.equals(msg)) {
            messages.put(msg.getId(), Message.copyOf(msg));
        }
        logger.info("Messages id:{}", messages.keySet());
    }

    @Override
    public Optional<Message> findMessageById(long id) {
        Message stored = messages.get(id);
        if (stored == null) {
            return Optional.empty();
        }
        return Optional.of(Message.copyOf(stored));
    }
}
