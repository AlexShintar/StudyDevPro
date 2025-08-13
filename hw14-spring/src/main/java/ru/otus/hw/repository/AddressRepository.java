package ru.otus.hw.repository;

import org.springframework.data.repository.CrudRepository;
import ru.otus.hw.model.Address;

public interface AddressRepository extends CrudRepository<Address, Long> {}
