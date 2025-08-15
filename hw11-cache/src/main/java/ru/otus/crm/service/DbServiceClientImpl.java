package ru.otus.crm.service;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.cache.HwCache;
import ru.otus.cache.HwListener;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.sessionmanager.TransactionManager;
import ru.otus.crm.model.Client;

public class DbServiceClientImpl implements DBServiceClient {
    private static final Logger log = LoggerFactory.getLogger(DbServiceClientImpl.class);

    private final DataTemplate<Client> clientDataTemplate;
    private final TransactionManager transactionManager;

    private final HwCache<String, Client> cache;

    public DbServiceClientImpl(
            TransactionManager transactionManager,
            DataTemplate<Client> clientDataTemplate,
            HwCache<String, Client> cache) {
        this.transactionManager = transactionManager;
        this.clientDataTemplate = clientDataTemplate;
        this.cache = cache;
        HwListener<String, Client> listener =
                (key, value, action) -> log.info("notify: key={}, value={}, action={}", key, value, action);
        this.cache.addListener(listener);
    }

    @Override
    public Client saveClient(Client client) {
        return transactionManager.doInTransaction(session -> {
            var clientCloned = client.clone();
            final Client savedClient;
            if (clientCloned.getId() == null) {
                savedClient = clientDataTemplate.insert(session, clientCloned);
                log.info("created client: {}", savedClient);
            } else {
                savedClient = clientDataTemplate.update(session, clientCloned);
                log.info("updated client: {}", savedClient);
            }
            cache.put("id" + savedClient.getId(), savedClient);
            return savedClient;
        });
    }

    @Override
    public Optional<Client> getClient(long id) {
        Client cachedClient = cache.get("id" + id);
        if (cachedClient != null) {
            return Optional.of(cachedClient);
        }
        return transactionManager.doInReadOnlyTransaction(session -> {
            var clientOptional = clientDataTemplate.findById(session, id);
            clientOptional.ifPresent(found -> {
                log.info("client: {}", found);
                cache.put("id" + found.getId(), found);
            });
            return clientOptional;
        });
    }

    @Override
    public List<Client> findAll() {
        return transactionManager.doInReadOnlyTransaction(session -> {
            var clientList = clientDataTemplate.findAll(session);
            log.info("clientList:{}", clientList);
            clientList.forEach(c -> cache.put("id" + c.getId(), c));
            return clientList;
        });
    }
}
