package ru.otus.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.services.TemplateProcessor;

public class ClientsServlet extends HttpServlet {

    private static final String CLIENTS_TEMPLATE = "clients.html";
    private static final String TEMPLATE_ATTR_CLIENTS = "clients";

    private final transient DBServiceClient dbServiceClient;
    private final transient TemplateProcessor templateProcessor;

    public ClientsServlet(TemplateProcessor templateProcessor, DBServiceClient dbServiceClient) {
        this.templateProcessor = templateProcessor;
        this.dbServiceClient = dbServiceClient;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {
        Map<String, Object> paramsMap = new HashMap<>();

        List<Client> allClients = dbServiceClient.findAll();

        List<ClientDto> clientsDto = allClients.stream().map(ClientDto::from).collect(Collectors.toList());

        paramsMap.put(TEMPLATE_ATTR_CLIENTS, clientsDto);

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(templateProcessor.getPage(CLIENTS_TEMPLATE, paramsMap));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse response) throws IOException {
        String name = req.getParameter("name");
        String street = req.getParameter("street");
        String phonesString = req.getParameter("phones");

        Address address = new Address(null, street);
        Client client = new Client(null, name, address, new ArrayList<>());

        List<Phone> phoneList = getPhoneList(phonesString, client);
        client.setPhones(phoneList);

        dbServiceClient.saveClient(client);
        response.sendRedirect("/clients");
    }

    private List<Phone> getPhoneList(String phonesString, Client client) {
        if (phonesString == null || phonesString.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(phonesString.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(number -> new Phone(null, number, client))
                .collect(Collectors.toList());
    }

    @Getter
    public static class ClientDto {
        private final Long id;
        private final String name;
        private final String address;
        private final List<String> phones;

        private ClientDto(Client client) {
            this.id = client.getId();
            this.name = client.getName();
            this.address = client.getAddress() != null ? client.getAddress().getStreet() : "";
            this.phones = client.getPhones() != null
                    ? client.getPhones().stream().map(Phone::getNumber).collect(Collectors.toList())
                    : Collections.emptyList();
        }

        public static ClientDto from(Client client) {
            return new ClientDto(client);
        }
    }
}
