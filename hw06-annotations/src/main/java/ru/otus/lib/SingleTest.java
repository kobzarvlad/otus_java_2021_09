package ru.otus.lib;

import java.lang.reflect.Method;

public record SingleTest(
    Object instance,
    Method before,
    Method test,
    Method after
) {
}
