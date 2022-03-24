package ru.otus;

import org.hibernate.cfg.Configuration;
import ru.otus.repository.DataTemplateHibernate;
import ru.otus.repository.HibernateUtils;
import ru.otus.sessionmanager.TransactionManagerHibernate;
import ru.otus.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.model.Address;
import ru.otus.model.Client;
import ru.otus.model.Phone;
import ru.otus.dao.ClientDao;
import ru.otus.dao.ClientDaoImpl;
import ru.otus.server.ClientsWebServer;
import ru.otus.server.ClientsWebServerWithFilterBasedSecurity;
import ru.otus.services.ClientAuthService;
import ru.otus.services.ClientAuthServiceImpl;
import ru.otus.services.TemplateProcessor;
import ru.otus.services.TemplateProcessorImpl;

/*
    Полезные для демо ссылки

    // Стартовая страница
    http://localhost:8080

    // Страница пользователей
    http://localhost:8080/users

    // REST сервис
    http://localhost:8080/api/user/3
*/
public class WebServerWithFilterBasedSecurityDemo {
    private static final int WEB_SERVER_PORT = 8080;
    private static final String TEMPLATES_DIR = "/templates/";
    public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";


    public static void main(String[] args) throws Exception {

        var hibernateConfiguration = new Configuration().configure(HIBERNATE_CFG_FILE);
        migrateData(hibernateConfiguration);
        ClientDao clientDao = createClientDao(hibernateConfiguration);
        ClientAuthService clientAuthService = new ClientAuthServiceImpl(clientDao);
        TemplateProcessor templateProcessor = new TemplateProcessorImpl(TEMPLATES_DIR);

        ClientsWebServer clientsWebServer = new ClientsWebServerWithFilterBasedSecurity(WEB_SERVER_PORT, clientAuthService,
            clientDao, templateProcessor);

        clientsWebServer.start();
        clientsWebServer.join();
    }

    private static ClientDao createClientDao(Configuration configuration) {
        var sessionFactory = HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class);
        var transactionManager = new TransactionManagerHibernate(sessionFactory);
        var clientTemplate = new DataTemplateHibernate<>(Client.class);
        return new ClientDaoImpl(transactionManager, clientTemplate);
    }

    private static void migrateData(Configuration configuration) {
        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");
        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();
    }
}
