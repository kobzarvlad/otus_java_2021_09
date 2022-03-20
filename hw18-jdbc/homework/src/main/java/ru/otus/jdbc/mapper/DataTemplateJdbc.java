package ru.otus.jdbc.mapper;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.stream.Collectors;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.DataTemplateException;
import ru.otus.core.repository.executor.DbExecutor;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * Сохратяет объект в базу, читает объект из базы
 */
public class DataTemplateJdbc<T> implements DataTemplate<T> {

    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData entitySQLMetaData;
    private final EntityClassMetaData<T> entityClassMetaData;

    public DataTemplateJdbc(DbExecutor dbExecutor, EntitySQLMetaData entitySQLMetaData, EntityClassMetaData<T> entityClassMetaData) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        return dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectByIdSql(), List.of(id), this::mapToEntity);
    }

    @Override
    public List<T> findAll(Connection connection) {
        return dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectAllSql(), List.of(), rs -> {
            try {
                List<T> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(mapToEntity(rs));
                }
                return result;
            } catch (Exception e) {
                throw new DataTemplateException(e);
            }
        }).orElse(List.of());
    }

    @Override
    public long insert(Connection connection, T client) {
        return dbExecutor.executeStatement(connection, entitySQLMetaData.getInsertSql(), getValuesOfFieldsWithoutId(client));
    }

    @Override
    public void update(Connection connection, T client) {
        List<Object> fieldValues = getValuesOfFieldsWithoutId(client);

        try {
            Field idField = entityClassMetaData.getIdField();
            idField.setAccessible(true);
            fieldValues.add(idField.get(client));
        } catch (IllegalAccessException e) {
            throw new DataTemplateException(e);
        }
        dbExecutor.executeStatement(connection, entitySQLMetaData.getUpdateSql(), fieldValues);
    }

    private T mapToEntity(ResultSet rs) {
        try {
            T result = null;
            if (rs.next()) {
                result = entityClassMetaData.getConstructor().newInstance();
                for (Field field : entityClassMetaData.getAllFields()) {
                    field.setAccessible(true);
                    field.set(result, rs.getObject(field.getName()));
                }
            }
            return result;
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }

    private List<Object> getValuesOfFieldsWithoutId(T client) {
        return entityClassMetaData.getFieldsWithoutId().stream()
            .map(field -> {
                try {
                    field.setAccessible(true);
                    return field.get(client);
                } catch (Exception e) {
                    throw new DataTemplateException(e);
                }
            })
            .collect(Collectors.toList());
    }
}
