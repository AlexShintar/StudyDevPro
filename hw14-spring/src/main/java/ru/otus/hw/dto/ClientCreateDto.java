package ru.otus.hw.dto;

import java.util.List;

public record ClientCreateDto(String name, String address, List<String> phones) {}
