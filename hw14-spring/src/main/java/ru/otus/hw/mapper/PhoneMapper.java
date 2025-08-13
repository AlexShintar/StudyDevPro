package ru.otus.hw.mapper;

import org.mapstruct.Mapper;
import ru.otus.hw.dto.PhoneDto;
import ru.otus.hw.model.Phone;

@Mapper(componentModel = "spring")
public interface PhoneMapper {

    PhoneDto toDto(Phone phone);

    Phone toEntity(PhoneDto dto);
}
