package ru.otus.jdbc.demo;

import java.util.ArrayList;
import java.util.List;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import ru.otus.cachehw.HwCache;
import ru.otus.cachehw.MyCache;
import ru.otus.jdbc.core.repository.executor.DbExecutorImpl;
import ru.otus.jdbc.core.sessionmanager.TransactionRunnerJdbc;
import ru.otus.jdbc.crm.datasource.DriverManagerDataSource;
import ru.otus.jdbc.crm.model.Client;
import ru.otus.jdbc.crm.repository.ClientDataTemplateJdbc;
import ru.otus.jdbc.crm.service.DbServiceClientImpl;

public class DbServiceDemo {
    private static final String URL = "jdbc:postgresql://localhost:5430/demoDB";
    private static final String USER = "usr";
    private static final String PASSWORD = "pwd";

    private static final Logger log = LoggerFactory.getLogger(DbServiceDemo.class);

    public static void main(String[] args) {
        var dataSource = new DriverManagerDataSource(URL, USER, PASSWORD);
        flywayMigrations(dataSource);
        var transactionRunner = new TransactionRunnerJdbc(dataSource);
        var dbExecutor = new DbExecutorImpl();
///
        var clientTemplate = new ClientDataTemplateJdbc(dbExecutor); //реализация DataTemplate, заточена на Client
        var clientCache = new MyCache<String, Client>();
///
        // without cache
        var dbServiceClientWithoutCache = new DbServiceClientImpl(transactionRunner, clientTemplate, clientCache, false);
        List<Long> idsWithoutCache = new ArrayList<>();
        for (int i = 0; i < 10_000; i++) {
            idsWithoutCache.add(dbServiceClientWithoutCache.saveClient(new Client("name" + i)).getId());
        }
        long startTimeWithoutCache = System.currentTimeMillis();
        idsWithoutCache.forEach(dbServiceClientWithoutCache::getClient);
        long endTimeWithoutCache = System.currentTimeMillis();

        // with cache
        var dbServiceClientWithCache = new DbServiceClientImpl(transactionRunner, clientTemplate, clientCache, true);
        List<Long> idsWithCache = new ArrayList<>();
        for (int i = 0; i < 10_000; i++) {
            idsWithCache.add(dbServiceClientWithCache.saveClient(new Client("name" + i)).getId());
        }
        long startTimeWithCache = System.currentTimeMillis();
        idsWithCache.forEach(dbServiceClientWithCache::getClient);
        long endTimeWithCache = System.currentTimeMillis();

        log.info("Selected 10_000 clients without cache. Spent time = {}", endTimeWithoutCache - startTimeWithoutCache);
        log.info("Selected 10_000 clients with cache. Spent time = {}", endTimeWithCache - startTimeWithCache);
    }

    private static void flywayMigrations(DataSource dataSource) {
        log.info("db migration started...");
        var flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:/db/migration")
                .load();
        flyway.clean();
        flyway.migrate();
        log.info("db migration finished.");
        log.info("***");
    }
}
