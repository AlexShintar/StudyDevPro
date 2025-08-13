package ru.otus.hw.mapper;

import org.mapstruct.Mapper;
import ru.otus.hw.dto.ClientDto;
import ru.otus.hw.model.Client;

@Mapper(
        componentModel = "spring",
        uses = {AddressMapper.class, PhoneMapper.class})
public interface ClientMapper {

    ClientDto toDto(Client client);

    Client toEntity(ClientDto dto);
}
