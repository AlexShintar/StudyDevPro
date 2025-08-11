package ru.otus.appcontainer;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;

public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

    private record ComponentDefinition(Method method, Object configInstance) {}

    public AppComponentsContainerImpl(String packageName) {
        List<Class<?>> configClasses = scanPackage(packageName);
        processConfigs(configClasses.toArray(new Class<?>[0]));
    }

    public AppComponentsContainerImpl(Class<?>... initialConfigClasses) {
        processConfigs(initialConfigClasses);
    }

    private List<Class<?>> scanPackage(String packageName) {
        List<Class<?>> configClasses = new ArrayList<>();
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String path = packageName.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File directory = new File(resource.toURI());
                if (directory.exists() && directory.isDirectory()) {
                    for (File file : Objects.requireNonNull(directory.listFiles())) {
                        if (file.getName().endsWith(".class")) {
                            String className = packageName
                                    + '.'
                                    + file.getName().substring(0, file.getName().length() - 6);
                            Class<?> clazz = Class.forName(className);
                            if (clazz.isAnnotationPresent(AppComponentsContainerConfig.class)) {
                                configClasses.add(clazz);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to scan package: " + packageName, e);
        }
        return configClasses;
    }

    private void processConfigs(Class<?>[] configClasses) {
        List<Class<?>> sortedConfigClasses = Arrays.stream(configClasses)
                .peek(this::checkConfigClass)
                .sorted(Comparator.comparingInt((Class<?> c) -> c.getAnnotation(AppComponentsContainerConfig.class)
                                .order())
                        .thenComparing(Class::getName))
                .toList();

        List<ComponentDefinition> componentDefinitions = new ArrayList<>();
        for (Class<?> configClass : sortedConfigClasses) {
            final Object configInstance = instantiateConfig(configClass);
            Arrays.stream(configClass.getDeclaredMethods())
                    .filter(m -> m.isAnnotationPresent(AppComponent.class))
                    .forEach(method -> componentDefinitions.add(new ComponentDefinition(method, configInstance)));
        }

        componentDefinitions.sort(Comparator.comparingInt((ComponentDefinition def) ->
                        def.method().getAnnotation(AppComponent.class).order())
                .thenComparing(def -> def.method().getName()));

        for (ComponentDefinition definition : componentDefinitions) {
            Method method = definition.method();
            Object configInstance = definition.configInstance();

            AppComponent meta = method.getAnnotation(AppComponent.class);
            String componentName = meta.name();

            if (appComponentsByName.containsKey(componentName)) {
                throw new DuplicateComponentNameException(
                        "Component with name '" + componentName + "' is already registered");
            }

            Object[] args = Arrays.stream(method.getParameterTypes())
                    .map(this::resolveDependencyByType)
                    .toArray();

            try {
                method.setAccessible(true);
                Object component = method.invoke(configInstance, args);
                if (component == null) {
                    throw new IllegalStateException("Factory method " + method.getName() + " returned null");
                }
                appComponents.add(component);
                appComponentsByName.put(componentName, component);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new ComponentCreationException(
                        "Failed to create component '" + componentName + "' from method " + method.getName(), e);
            }
        }
    }

    private Object instantiateConfig(Class<?> configClass) {
        try {
            Constructor<?> ctor = configClass.getDeclaredConstructor();
            ctor.setAccessible(true);
            return ctor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate config class " + configClass.getName(), e);
        }
    }

    private Object resolveDependencyByType(Class<?> type) {
        List<Object> candidates = appComponents.stream()
                .filter(obj -> type.isAssignableFrom(obj.getClass()))
                .toList();

        if (candidates.isEmpty()) {
            throw new NoSuchComponentException("No component found for dependency type: " + type.getName());
        }
        if (candidates.size() > 1) {
            String types = candidates.stream()
                    .map(o -> o.getClass().getName())
                    .distinct()
                    .collect(Collectors.joining(", "));
            throw new AmbiguousComponentException(
                    "Multiple components found for type " + type.getName() + ": " + types);
        }
        return candidates.getFirst();
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not a config: %s", configClass.getName()));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C> C getAppComponent(Class<C> componentClass) {
        return (C) resolveDependencyByType(componentClass);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C> C getAppComponent(String componentName) {
        Object bean = appComponentsByName.get(componentName);
        if (bean == null) {
            throw new NoSuchComponentException("Component with name '" + componentName + "' not found");
        }
        return (C) bean;
    }

    public static class DuplicateComponentNameException extends RuntimeException {
        public DuplicateComponentNameException(String message) {
            super(message);
        }
    }

    public static class NoSuchComponentException extends RuntimeException {
        public NoSuchComponentException(String message) {
            super(message);
        }
    }

    public static class AmbiguousComponentException extends RuntimeException {
        public AmbiguousComponentException(String message) {
            super(message);
        }
    }

    public static class ComponentCreationException extends RuntimeException {
        public ComponentCreationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
