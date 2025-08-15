package ru.otus;

import org.hibernate.cfg.Configuration;
import ru.otus.core.repository.DataTemplateHibernate;
import ru.otus.core.repository.HibernateUtils;
import ru.otus.core.sessionmanager.TransactionManagerHibernate;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.crm.service.DbInitializer;
import ru.otus.crm.service.DbServiceClientImpl;
import ru.otus.server.ClientsWebServerWithBasicSecurity;
import ru.otus.services.AuthServiceFactory;
import ru.otus.services.TemplateProcessorImpl;

/*
    // Стартовая страница
    http://localhost:8080

    // Список клиентов (логин/пароль admin)
    http://localhost:8080/clients

*/
public class WebServerWithBasicSecurityDemo {
    private static final int WEB_SERVER_PORT = 8080;
    private static final String TEMPLATES_DIR = "/templates/";
    private static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";

    public static void main(String[] args) throws Exception {
        var cfg = new Configuration().configure(HIBERNATE_CFG_FILE);

        var url = cfg.getProperty("hibernate.connection.url");
        var user = cfg.getProperty("hibernate.connection.username");
        var pass = cfg.getProperty("hibernate.connection.password");

        new DbInitializer(url, user, pass, null).executeMigrations();

        var dbServiceClient = getDbServiceClient(cfg);

        new DbInitializer(url, user, pass, dbServiceClient).populateDbWithRandomData();

        var authService = new AuthServiceFactory().createAuthService();

        var templateProcessor = new TemplateProcessorImpl(TEMPLATES_DIR);

        var clientsWebServer =
                new ClientsWebServerWithBasicSecurity(WEB_SERVER_PORT, authService, dbServiceClient, templateProcessor);

        clientsWebServer.start();
        clientsWebServer.join();
    }

    private static DBServiceClient getDbServiceClient(Configuration hibernateConfig) {
        var sessionFactory =
                HibernateUtils.buildSessionFactory(hibernateConfig, Client.class, Address.class, Phone.class);
        var transactionManager = new TransactionManagerHibernate(sessionFactory);
        var clientTemplate = new DataTemplateHibernate<>(Client.class);
        return new DbServiceClientImpl(transactionManager, clientTemplate);
    }
}
