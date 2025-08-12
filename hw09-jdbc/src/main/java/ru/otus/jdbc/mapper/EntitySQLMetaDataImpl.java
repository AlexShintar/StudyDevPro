package ru.otus.jdbc.mapper;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.stream.Collectors;

public class EntitySQLMetaDataImpl<T> implements EntitySQLMetaData {
    private static final String SELECT_ALL_TEMPLATE = "select * from %s";
    private static final String SELECT_BY_ID_TEMPLATE = "select * from %s where %s = ?";
    private static final String INSERT_TEMPLATE = "insert into %s (%s) values (%s)";
    private static final String UPDATE_TEMPLATE = "update %s set %s where %s = ?";

    private final EntityClassMetaData<T> metaData;

    private String insertSql;
    private String updateSql;

    public EntitySQLMetaDataImpl(EntityClassMetaData<T> metaData) {
        this.metaData = Objects.requireNonNull(metaData, "metaData cannot be null");
        validateMetaData();
    }

    @Override
    public String getSelectAllSql() {
        return String.format(SELECT_ALL_TEMPLATE, metaData.getName());
    }

    @Override
    public String getSelectByIdSql() {
        return String.format(
                SELECT_BY_ID_TEMPLATE, metaData.getName(), metaData.getIdField().getName());
    }

    @Override
    public String getInsertSql() {
        if (insertSql == null) {
            insertSql = String.format(INSERT_TEMPLATE, metaData.getName(), getFieldNames(), getPlaceholders());
        }
        return insertSql;
    }

    @Override
    public String getUpdateSql() {
        if (updateSql == null) {
            updateSql = String.format(
                    UPDATE_TEMPLATE,
                    metaData.getName(),
                    getUpdateSetClause(),
                    metaData.getIdField().getName());
        }
        return updateSql;
    }

    private void validateMetaData() {
        if (metaData.getIdField() == null) {
            throw new IllegalArgumentException("ID field is required");
        }
        if (metaData.getFieldsWithoutId().isEmpty()) {
            throw new IllegalArgumentException("At least one non-ID field is required");
        }
    }

    private String getUpdateSetClause() {
        return metaData.getFieldsWithoutId().stream()
                .map(field -> field.getName() + " = ?")
                .collect(Collectors.joining(", "));
    }

    private String getPlaceholders() {
        return metaData.getFieldsWithoutId().stream().map(field -> "?").collect(Collectors.joining(", "));
    }

    private String getFieldNames() {
        return metaData.getFieldsWithoutId().stream().map(Field::getName).collect(Collectors.joining(", "));
    }
}
