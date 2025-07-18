package ru.otus.jdbc.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {
    private final Class<T> entityClass;

    private String className;
    private Constructor<T> constructor;
    private Field idField;
    private List<Field> allFields;
    private List<Field> fieldsWithoutId;

    public EntityClassMetaDataImpl(Class<T> entityClass) {
        this.entityClass = Objects.requireNonNull(entityClass, "Entity class cannot be null");
    }

    @Override
    public String getName() {
        if (className == null) {
            className = entityClass.getSimpleName();
        }
        return className;
    }

    @Override
    public Constructor<T> getConstructor() {
        if (constructor == null) {
            constructor = findDefaultConstructor();
        }
        return constructor;
    }

    @Override
    public Field getIdField() {
        if (idField == null) {
            idField = findIdField();
        }
        return idField;
    }

    @Override
    public List<Field> getAllFields() {
        if (allFields == null) {
            allFields = Arrays.stream(entityClass.getDeclaredFields()).toList();
        }
        return allFields;
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        if (fieldsWithoutId == null) {
            fieldsWithoutId = getAllFields().stream()
                    .filter(field -> !field.equals(getIdField()))
                    .toList();
        }
        return fieldsWithoutId;
    }

    private Constructor<T> findDefaultConstructor() {
        try {
            return entityClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Default constructor not found for class: " + entityClass.getName(), e);
        }
    }

    private Field findIdField() {
        return getAllFields().stream()
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Id field not found in class: " + entityClass.getName()));
    }
}
