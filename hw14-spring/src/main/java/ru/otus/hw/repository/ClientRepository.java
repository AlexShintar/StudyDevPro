package ru.otus.hw.repository;

import org.springframework.data.repository.ListCrudRepository;
import ru.otus.hw.model.Client;

public interface ClientRepository extends ListCrudRepository<Client, Long> {}
