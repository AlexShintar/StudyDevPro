package ru.otus.hw.service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.ClientCreateDto;
import ru.otus.hw.dto.ClientDto;

public interface ClientService {

    ClientDto findById(long id);

    List<ClientDto> findAll();

    @Transactional
    ClientDto insert(ClientCreateDto clientCreateDto);
}
