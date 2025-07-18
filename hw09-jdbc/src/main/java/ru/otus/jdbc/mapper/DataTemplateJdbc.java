package ru.otus.jdbc.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.DataTemplateException;
import ru.otus.core.repository.executor.DbExecutor;

/** Сохраняет объект в базу, читает объект из базы */
public class DataTemplateJdbc<T> implements DataTemplate<T> {
    private static final String FIND_DATA_ERROR = "Can't find data for class %s";
    private static final String INSERT_FAILED_ERROR = "Failed to insert entity: %s";
    private static final String UPDATE_FAILED_ERROR = "Failed to update entity: %s";
    private static final String MAP_SINGLE_RESULT_ERROR = "Failed to map result set to object";
    private static final String MAP_RESULT_LIST_ERROR = "Failed to map result set to object list";
    private static final String EXTRACT_FIELD_ERROR = "Failed to extract field value: %s";
    private static final String SET_FIELD_ERROR = "Failed to set field value: %s";

    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData entitySQLMetaData;
    private final EntityClassMetaData<T> classMetaData;

    public DataTemplateJdbc(
            DbExecutor dbExecutor, EntitySQLMetaData entitySQLMetaData, EntityClassMetaData<T> classMetaData) {
        this.dbExecutor = Objects.requireNonNull(dbExecutor, "dbExecutor cannot be null");
        this.entitySQLMetaData = Objects.requireNonNull(entitySQLMetaData, "entitySQLMetaData cannot be null");
        this.classMetaData = Objects.requireNonNull(classMetaData, "classMetaData cannot be null");
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        return dbExecutor.executeSelect(
                connection, entitySQLMetaData.getSelectByIdSql(), List.of(id), this::mapSingleResult);
    }

    @Override
    public List<T> findAll(Connection connection) {
        return dbExecutor
                .executeSelect(
                        connection, entitySQLMetaData.getSelectAllSql(), Collections.emptyList(), this::mapResultList)
                .orElseThrow(() -> new DataTemplateException(String.format(FIND_DATA_ERROR, classMetaData.getName())));
    }

    @Override
    public long insert(Connection connection, T entity) {
        Objects.requireNonNull(entity, "entity cannot be null");

        try {
            List<Object> params = extractFieldValues(entity, classMetaData.getFieldsWithoutId());
            return dbExecutor.executeStatement(connection, entitySQLMetaData.getInsertSql(), params);
        } catch (Exception e) {
            throw new DataTemplateException(String.format(INSERT_FAILED_ERROR, entity), e);
        }
    }

    @Override
    public void update(Connection connection, T entity) {
        Objects.requireNonNull(entity, "entity cannot be null");

        try {
            List<Object> params = buildUpdateParams(entity);
            dbExecutor.executeStatement(connection, entitySQLMetaData.getUpdateSql(), params);
        } catch (Exception e) {
            throw new DataTemplateException(String.format(UPDATE_FAILED_ERROR, entity), e);
        }
    }

    private T mapSingleResult(ResultSet rs) {
        try {
            return rs.next() ? constructObject(rs) : null;
        } catch (SQLException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new DataTemplateException(MAP_SINGLE_RESULT_ERROR, e);
        }
    }

    private List<T> mapResultList(ResultSet rs) {
        List<T> result = new ArrayList<>();
        try {
            while (rs.next()) {
                result.add(constructObject(rs));
            }
            return result;
        } catch (SQLException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new DataTemplateException(MAP_RESULT_LIST_ERROR, e);
        }
    }

    private List<Object> buildUpdateParams(T entity) {
        List<Object> params = new ArrayList<>(extractFieldValues(entity, classMetaData.getFieldsWithoutId()));
        params.add(getFieldValue(entity, classMetaData.getIdField()));
        return params;
    }

    private List<Object> extractFieldValues(T entity, List<Field> fields) {
        return fields.stream().map(field -> getFieldValue(entity, field)).toList();
    }

    private Object getFieldValue(T entity, Field field) {
        try {
            field.setAccessible(true);
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new DataTemplateException(String.format(EXTRACT_FIELD_ERROR, field.getName()), e);
        }
    }

    private T constructObject(ResultSet rs)
            throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        T obj = classMetaData.getConstructor().newInstance();

        for (Field field : classMetaData.getAllFields()) {
            setFieldValue(obj, field, rs.getObject(field.getName()));
        }

        return obj;
    }

    private void setFieldValue(T obj, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            throw new DataTemplateException(String.format(SET_FIELD_ERROR, field.getName()), e);
        }
    }
}
