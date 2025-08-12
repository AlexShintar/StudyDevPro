package ru.otus.crm.service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.datafaker.Faker;
import ru.otus.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;

public class DbInitializer {

    private static final Faker FAKER = new Faker();

    private final String dbUrl;
    private final String dbUserName;
    private final String dbPassword;
    private final DBServiceClient dbServiceClient;

    public DbInitializer(String dbUrl, String dbUserName, String dbPassword, DBServiceClient dbServiceClient) {

        this.dbUrl = dbUrl;
        this.dbUserName = dbUserName;
        this.dbPassword = dbPassword;
        this.dbServiceClient = dbServiceClient;
    }

    public void executeMigrations() {
        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();
    }

    public void populateDbWithRandomData() {
        int numberOfUsers = ThreadLocalRandom.current().nextInt(5, 11);

        for (int i = 0; i < numberOfUsers; i++) {
            String name = FAKER.name().fullName();
            String streetAddress = FAKER.address().streetAddress();
            String cityName = FAKER.address().cityName();
            Address address = new Address(null, streetAddress + ", " + cityName);

            int numberOfPhones = ThreadLocalRandom.current().nextInt(1, 3);
            List<Phone> phones = new java.util.ArrayList<>();
            for (int j = 0; j < numberOfPhones; j++) {
                String phoneNumber = FAKER.phoneNumber().phoneNumber();
                phones.add(new Phone(null, phoneNumber));
            }

            dbServiceClient.saveClient(new Client(null, name, address, phones));
        }
    }
}
