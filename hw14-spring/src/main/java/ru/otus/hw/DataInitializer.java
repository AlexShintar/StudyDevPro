package ru.otus.hw;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.ClientCreateDto;
import ru.otus.hw.service.ClientService;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final ClientService clientService;

    private static final Faker FAKER = new Faker();

    @PostConstruct
    public void populateDbWithRandomData() {
        int numberOfUsers = ThreadLocalRandom.current().nextInt(5, 11);
        for (int i = 0; i < numberOfUsers; i++) {
            String name = FAKER.name().fullName();
            String streetAddress = FAKER.address().streetAddress();
            String cityName = FAKER.address().cityName();
            String fullAddress = streetAddress + ", " + cityName;

            int numberOfPhones = ThreadLocalRandom.current().nextInt(1, 4);
            List<String> phones = new ArrayList<>();
            for (int j = 0; j < numberOfPhones; j++) {
                String phoneNumber = FAKER.phoneNumber().phoneNumber();
                phones.add(phoneNumber);
            }

            ClientCreateDto clientCreateDto = new ClientCreateDto(name, fullAddress, phones);
            clientService.insert(clientCreateDto);
        }
    }
}
