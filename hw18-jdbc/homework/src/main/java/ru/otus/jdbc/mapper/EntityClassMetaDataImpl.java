package ru.otus.jdbc.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import ru.otus.core.annotation.Id;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {

  private final Class<T> clazz;

  public EntityClassMetaDataImpl(Class<T> clazz) {
    this.clazz = clazz;
  }

  @Override
  public String getName() {
    return clazz.getSimpleName().toLowerCase();
  }

  @Override
  public Constructor<T> getConstructor() {
    try {
      return clazz.getDeclaredConstructor();
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Field getIdField() {
    return Arrays.stream(clazz.getDeclaredFields())
        .filter(field -> field.isAnnotationPresent(Id.class))
        .findFirst()
        .orElseThrow();
  }

  @Override
  public List<Field> getAllFields() {
    return Arrays.stream(clazz.getDeclaredFields()).toList();
  }

  @Override
  public List<Field> getFieldsWithoutId() {
    return Arrays.stream(clazz.getDeclaredFields())
        .filter(field -> !field.isAnnotationPresent(Id.class))
        .toList();
  }
}
