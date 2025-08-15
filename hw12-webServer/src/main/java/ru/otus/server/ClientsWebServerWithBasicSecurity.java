package ru.otus.server;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.security.ConstraintMapping;
import org.eclipse.jetty.ee10.servlet.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.Constraint;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.Handler;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.services.TemplateProcessor;

public class ClientsWebServerWithBasicSecurity extends ClientsWebServerSimple {
    private static final String ROLE_NAME_ADMIN = "admin";

    private final LoginService loginService;

    public ClientsWebServerWithBasicSecurity(
            int port, LoginService loginService, DBServiceClient dbServiceClient, TemplateProcessor templateProcessor) {
        super(port, dbServiceClient, templateProcessor);
        this.loginService = loginService;
    }

    @Override
    protected Handler applySecurity(ServletContextHandler servletContextHandler, String... paths) {
        Constraint constraint = Constraint.from(ROLE_NAME_ADMIN);

        List<ConstraintMapping> mappings = new ArrayList<>();
        for (String path : paths) {
            ConstraintMapping m = new ConstraintMapping();
            m.setPathSpec(path);
            m.setConstraint(constraint);
            mappings.add(m);
        }

        ConstraintSecurityHandler security = new ConstraintSecurityHandler();
        security.setAuthenticator(new BasicAuthenticator());
        security.setLoginService(loginService);
        security.setConstraintMappings(mappings);

        security.setHandler(new Handler.Wrapper(servletContextHandler));
        return security;
    }
}
