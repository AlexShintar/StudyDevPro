package ru.otus.cache;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.core.repository.DataTemplateHibernate;
import ru.otus.core.repository.HibernateUtils;
import ru.otus.core.sessionmanager.TransactionManagerHibernate;
import ru.otus.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.crm.service.DbServiceClientImpl;
import ru.otus.crm.service.NoCacheDbServiceClientImpl;

@DisplayName("MyCache должен")
class MyCacheTest {

    private static final Logger log = LoggerFactory.getLogger(MyCacheTest.class);
    public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";
    private static final int TEST_OPERATIONS = 1000;

    private TransactionManagerHibernate transactionManager;
    private DataTemplateHibernate<Client> clientTemplate;
    private NoCacheDbServiceClientImpl regularService;
    private DbServiceClientImpl cachedService;
    private List<Long> testClientIds;
    private SessionFactory sessionFactory;

    @BeforeEach
    void setUp() {
        var configuration = new Configuration().configure(HIBERNATE_CFG_FILE);
        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();

        sessionFactory = HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class);
        transactionManager = new TransactionManagerHibernate(sessionFactory);

        var sessionFactory =
                HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class);
        transactionManager = new TransactionManagerHibernate(sessionFactory);
        clientTemplate = new DataTemplateHibernate<>(Client.class);

        regularService = new NoCacheDbServiceClientImpl(transactionManager, clientTemplate);

        HwCache<String, Client> cache = new MyCache<>();
        cachedService = new DbServiceClientImpl(transactionManager, clientTemplate, cache);

        prepareTestData();
    }

    @AfterEach
    void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    private void prepareTestData() {
        testClientIds = new ArrayList<>();
        log.info("Preparing test data...");

        for (int i = 0; i < 100; i++) {
            Client client = regularService.saveClient(new Client("TestClient" + i));
            testClientIds.add(client.getId());
        }

        log.info("Test data prepared: {} clients", testClientIds.size());
    }

    @Test
    void compareReadPerformance() {
        log.info("Starting read performance comparison...");

        long regularServiceTime = measureReadOperations(regularService, "Regular Service");

        long cachedServiceTime = measureReadOperations(cachedService, "Cached Service");

        log.info("=== PERFORMANCE COMPARISON RESULTS ===");
        log.info("Regular Service: {} ms", regularServiceTime);
        log.info("Cached Service: {} ms", cachedServiceTime);

        if (cachedServiceTime < regularServiceTime) {
            long improvement = regularServiceTime - cachedServiceTime;
            double speedup = (double) regularServiceTime / cachedServiceTime;
            log.info("Cached service is {}x faster ({} ms improvement)", String.format("%.2f", speedup), improvement);
        } else {
            log.info("Regular service performed better this time");
        }

        assertTrue(
                cachedServiceTime <= regularServiceTime * 1.1,
                "Cached service should be faster or comparable to regular service");
    }

    @Test
    void compareMixedOperationsPerformance() {
        log.info("Starting mixed operations performance comparison...");

        long regularServiceTime = measureMixedOperations(regularService, "Regular Service");

        long cachedServiceTime = measureMixedOperations(cachedService, "Cached Service");

        log.info("=== MIXED OPERATIONS PERFORMANCE RESULTS ===");
        log.info("Regular Service: {} ms", regularServiceTime);
        log.info("Cached Service: {} ms", cachedServiceTime);

        double difference = Math.abs(regularServiceTime - cachedServiceTime);
        log.info("Time difference: {} ms", difference);
    }

    private long measureReadOperations(DBServiceClient service, String serviceName) {
        log.info("Testing {} - {} read operations", serviceName, TEST_OPERATIONS);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < TEST_OPERATIONS; i++) {
            Long randomClientId = testClientIds.get(ThreadLocalRandom.current().nextInt(testClientIds.size()));

            service.getClient(randomClientId);

            if (i % 100 == 0) {
                log.debug("{} - Completed {} operations", serviceName, i);
            }
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        log.info("{} completed {} read operations in {} ms", serviceName, TEST_OPERATIONS, totalTime);

        return totalTime;
    }

    private long measureMixedOperations(DBServiceClient service, String serviceName) {
        log.info("Testing {} - {} mixed operations", serviceName, TEST_OPERATIONS);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < TEST_OPERATIONS; i++) {
            int operation = ThreadLocalRandom.current().nextInt(10);

            if (operation < 7) {
                Long randomClientId =
                        testClientIds.get(ThreadLocalRandom.current().nextInt(testClientIds.size()));
                service.getClient(randomClientId);
            } else if (operation < 9) {
                Client newClient = service.saveClient(new Client("MixedTest" + i));
                testClientIds.add(newClient.getId());
            } else {
                Long randomClientId =
                        testClientIds.get(ThreadLocalRandom.current().nextInt(testClientIds.size()));
                var existingClient = service.getClient(randomClientId);
                if (existingClient.isPresent()) {
                    Client updated = new Client(randomClientId, "Updated" + i);
                    service.saveClient(updated);
                }
            }

            if (i % 100 == 0) {
                log.debug("{} - Completed {} mixed operations", serviceName, i);
            }
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        log.info("{} completed {} mixed operations in {} ms", serviceName, TEST_OPERATIONS, totalTime);

        return totalTime;
    }

    @Test
    void warmupCacheAndCompare() {
        log.info("Testing with cache warmup...");

        log.info("Warming up cache...");
        for (Long clientId : testClientIds) {
            cachedService.getClient(clientId);
        }
        log.info("Cache warmed up");

        long regularServiceTime = measureReadOperations(regularService, "Regular Service (after warmup)");
        long cachedServiceTime = measureReadOperations(cachedService, "Cached Service (after warmup)");

        log.info("=== PERFORMANCE WITH WARMED CACHE ===");
        log.info("Regular Service: {} ms", regularServiceTime);
        log.info("Cached Service: {} ms", cachedServiceTime);

        if (cachedServiceTime < regularServiceTime) {
            double speedup = (double) regularServiceTime / cachedServiceTime;
            log.info("Cache provides {}x speedup", String.format("%.2f", speedup));
        }

        assertTrue(cachedServiceTime < regularServiceTime, "Warmed cache should be faster than regular service");
    }
}
