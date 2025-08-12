package ru.otus.services;

import java.net.URI;
import java.net.URISyntaxException;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.util.resource.PathResourceFactory;
import org.eclipse.jetty.util.resource.Resource;
import ru.otus.helpers.FileSystemHelper;

public class AuthServiceFactory {

    private static final String HASH_LOGIN_SERVICE_CONFIG_NAME = "realm.properties";
    private static final String REALM_NAME = "AnyRealm";

    public LoginService createAuthService() {
        try {
            String hashLoginServiceConfigPath =
                    FileSystemHelper.localFileNameOrResourceNameToFullPath(HASH_LOGIN_SERVICE_CONFIG_NAME);
            PathResourceFactory pathResourceFactory = new PathResourceFactory();
            Resource configResource = pathResourceFactory.newResource(new URI(hashLoginServiceConfigPath));
            return new HashLoginService(REALM_NAME, configResource);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error creating auth service", e);
        }
    }
}
