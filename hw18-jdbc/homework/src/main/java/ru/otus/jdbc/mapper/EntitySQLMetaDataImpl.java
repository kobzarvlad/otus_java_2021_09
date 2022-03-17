package ru.otus.jdbc.mapper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class EntitySQLMetaDataImpl<T> implements EntitySQLMetaData {

  private final EntityClassMetaData<T> entityClassMetaData;

  public EntitySQLMetaDataImpl(EntityClassMetaData<T> entityClassMetaData) {
    this.entityClassMetaData = entityClassMetaData;
  }

  @Override
  public String getSelectAllSql() {
    return String.format("SELECT * FROM %s", entityClassMetaData.getName());
  }

  @Override
  public String getSelectByIdSql() {
    String allFieldsString = entityClassMetaData.getAllFields().stream().map(Field::getName).collect(Collectors.joining(","));

    return String.format("SELECT %s FROM %s WHERE %s = ?", allFieldsString, getTableName(), getIdFieldName());
  }

  @Override
  public String getInsertSql() {
    List<Field> fieldsWithoutId = entityClassMetaData.getFieldsWithoutId();
    String fieldsWithoutIdString = fieldsWithoutId.stream().map(Field::getName).collect(Collectors.joining(","));
    String questionMarks = fieldsWithoutId.stream().map(field -> "?").collect(Collectors.joining(","));

    return String.format("INSERT INTO %s (%s) VALUES(%s)", getTableName(), fieldsWithoutIdString, questionMarks);
  }

  @Override
  public String getUpdateSql() {
    String setExpression = entityClassMetaData.getFieldsWithoutId().stream()
        .map(field -> String.format("%s = ?", field.getName()))
        .collect(Collectors.joining(","));

    return String.format("UPDATE %s SET %s WHERE %s = ?", getTableName(), setExpression, getIdFieldName());
  }

  private String getTableName() {
    return entityClassMetaData.getName();
  }

  private String getIdFieldName() {
    return entityClassMetaData.getIdField().getName();
  }
}
