package ru.otus.hw.repository;

import org.springframework.data.repository.ListCrudRepository;
import ru.otus.hw.model.Phone;

public interface PhoneRepository extends ListCrudRepository<Phone, Long> {}
