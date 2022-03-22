package ru.otus.jdbc.crm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import ru.otus.cachehw.HwCache;
import ru.otus.cachehw.HwListener;
import ru.otus.jdbc.core.repository.DataTemplate;
import ru.otus.jdbc.core.sessionmanager.TransactionRunner;
import ru.otus.jdbc.crm.model.Client;

public class DbServiceClientImpl implements DBServiceClient {
    private static final Logger log = LoggerFactory.getLogger(DbServiceClientImpl.class);

    private final DataTemplate<Client> dataTemplate;
    private final TransactionRunner transactionRunner;
    private final HwCache<String, Client> cache;
    private final boolean useCache;

    public DbServiceClientImpl(
        TransactionRunner transactionRunner,
        DataTemplate<Client> dataTemplate,
        HwCache<String, Client> cache,
        boolean useCache
    ) {
        this.transactionRunner = transactionRunner;
        this.dataTemplate = dataTemplate;
        this.cache = cache;
        HwListener<String, Client> listener = new HwListener<String, Client>() {
            @Override
            public void notify(String key, Client value, String action) {
                log.info("key:{}, value:{}, action: {}", key, value, action);
            }
        };
        this.cache.addListener(listener);
        this.useCache = useCache;
    }

    @Override
    public Client saveClient(Client client) {
        Client savedClient = transactionRunner.doInTransaction(connection -> {
            if (client.getId() == null) {
                var clientId = dataTemplate.insert(connection, client);
                var createdClient = new Client(clientId, client.getName());
                log.info("created client: {}", createdClient);
                return createdClient;
            }
            dataTemplate.update(connection, client);
            log.info("updated client: {}", client);
            return client;
        });
        if (useCache) {
            cache.put(savedClient.getId().toString(), savedClient);
        }
        return savedClient;
    }

    @Override
    public Optional<Client> getClient(long id) {
        if (useCache) {
            Client clientFromCache = cache.get(String.valueOf(id));
            if (clientFromCache != null) {
                return Optional.of(clientFromCache);
            }
        }
        return transactionRunner.doInTransaction(connection -> {
            var clientOptional = dataTemplate.findById(connection, id);
            log.info("client: {}", clientOptional);
            return clientOptional;
        });
    }

    @Override
    public List<Client> findAll() {
        return transactionRunner.doInTransaction(connection -> {
            var clientList = dataTemplate.findAll(connection);
            log.info("clientList:{}", clientList);
            return clientList;
       });
    }
}
