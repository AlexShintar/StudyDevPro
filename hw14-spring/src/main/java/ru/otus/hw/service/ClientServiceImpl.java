package ru.otus.hw.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.ClientCreateDto;
import ru.otus.hw.dto.ClientDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.ClientMapper;
import ru.otus.hw.model.Address;
import ru.otus.hw.model.Client;
import ru.otus.hw.model.Phone;
import ru.otus.hw.repository.ClientRepository;
import ru.otus.hw.repository.PhoneRepository;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    private final PhoneRepository phoneRepository;

    private final ClientMapper clientMapper;

    @Override
    public ClientDto findById(long id) {
        Client client = findByIdOrThrow(clientRepository.findById(id), Client.class, id);
        return clientMapper.toDto(client);
    }

    @Override
    public List<ClientDto> findAll() {
        return clientRepository.findAll().stream().map(clientMapper::toDto).toList();
    }

    @Transactional
    @Override
    public ClientDto insert(ClientCreateDto clientCreateDto) {
        Address address = new Address(null, clientCreateDto.address());
        Client client = new Client(null, clientCreateDto.name(), address);

        Client savedClient = clientRepository.save(client);

        if (clientCreateDto.phones() != null && !clientCreateDto.phones().isEmpty()) {
            List<Phone> phones = clientCreateDto.phones().stream()
                    .map(number -> new Phone(null, number, savedClient.getId()))
                    .toList();
            List<Phone> savedPhones = phoneRepository.saveAll(phones);
            savedClient.setPhones(new HashSet<>(savedPhones));
        }

        return clientMapper.toDto(savedClient);
    }

    private <T> T findByIdOrThrow(Optional<T> optional, Class<?> entityClass, long id) {
        return optional.orElseThrow(
                () -> new EntityNotFoundException(entityClass.getSimpleName() + " not found: " + id));
    }
}
