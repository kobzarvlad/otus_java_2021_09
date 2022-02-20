package ru.otus.lib;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

public record ClassInfo<T>(
    Class<T> clazz,
    Optional<Method> before,
    List<Method> tests,
    Optional<Method> after
) {
}
