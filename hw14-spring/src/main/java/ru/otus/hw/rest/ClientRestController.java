package ru.otus.hw.rest;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.ClientCreateDto;
import ru.otus.hw.dto.ClientDto;
import ru.otus.hw.service.ClientService;

@RestController
@RequiredArgsConstructor
public class ClientRestController {

    private final ClientService clientService;

    @GetMapping("/api/v1/client")
    public List<ClientDto> getAll() {
        return clientService.findAll();
    }

    @GetMapping("/api/v1/client/{id}")
    public ClientDto getClientById(@PathVariable long id) {
        return clientService.findById(id);
    }

    @PostMapping("/api/v1/client")
    public ClientDto createClient(@RequestBody ClientCreateDto clientCreateDto) {
        return clientService.insert(clientCreateDto);
    }
}
