package ru.otus.lib;

import java.lang.reflect.Method;
import java.util.List;

public record ClassInfo<T>(
    Class<T> clazz,
    Method before,
    List<Method> tests,
    Method after
) {
}
