package ru.otus.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.List;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.security.UserStore;
import org.eclipse.jetty.util.security.Password;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.crm.model.Client;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.services.TemplateProcessor;

@DisplayName("Интеграционный тест веб-сервера должен")
class ClientsWebServerImplTest {

    private static final int TEST_PORT = 8989;
    private static final String SERVER_URL = "http://localhost:" + TEST_PORT;
    private static final String CLIENTS_PAGE_URL = "/clients";

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "testpass";

    private static final Client TEST_CLIENT = new Client(1L, "Client");

    private static ClientsWebServer webServer;
    private static HttpClient httpClient;

    @BeforeAll
    static void setUp() throws Exception {
        httpClient = HttpClient.newHttpClient();

        DBServiceClient dbServiceClient = mock(DBServiceClient.class);
        TemplateProcessor templateProcessor = mock(TemplateProcessor.class);
        LoginService loginService = createTestLoginService();

        given(dbServiceClient.findAll()).willReturn(List.of(TEST_CLIENT));

        given(templateProcessor.getPage(eq("clients.html"), any()))
                .willReturn("<html><body><h1>Client List</h1><p>" + TEST_CLIENT.getName() + "</p></body></html>");

        webServer = new ClientsWebServerWithBasicSecurity(TEST_PORT, loginService, dbServiceClient, templateProcessor);

        webServer.start();
    }

    @AfterAll
    static void tearDown() throws Exception {
        if (webServer != null) {
            webServer.stop();
        }
    }

    private static LoginService createTestLoginService() {
        HashLoginService loginService = new HashLoginService("TestRealm");
        UserStore userStore = new UserStore();
        userStore.addUser(TEST_USERNAME, new Password(TEST_PASSWORD), new String[] {"admin"});
        loginService.setUserStore(userStore);
        return loginService;
    }

    @DisplayName("успешно загружать страницу клиентов после аутентификации")
    @Test
    void shouldSuccessfullyLoadClientsPageAfterAuthentication() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(SERVER_URL + CLIENTS_PAGE_URL))
                .header(
                        "Authorization",
                        "Basic " + Base64.getEncoder().encodeToString((TEST_USERNAME + ":" + TEST_PASSWORD).getBytes()))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(HttpURLConnection.HTTP_OK);
        assertThat(response.body()).contains(TEST_CLIENT.getName());
    }
}
