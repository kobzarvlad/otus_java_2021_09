package ru.otus.lib;

import java.lang.reflect.Method;
import java.util.Optional;

public record SingleTest<T>(
    T instance,
    Optional<Method> before,
    Method test,
    Optional<Method> after
) {
}
