package ru.otus.hw.dto;

import java.util.List;

public record ClientDto(Long id, String name, AddressDto address, List<PhoneDto> phones) {}
