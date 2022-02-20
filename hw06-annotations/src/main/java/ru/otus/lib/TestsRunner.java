package ru.otus.lib;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import ru.otus.annotations.After;
import ru.otus.annotations.Before;
import ru.otus.annotations.Test;

public class TestsRunner {

  public static <T> void runTests(Class<T> clazz) {
    List<SingleTest<T>> tests = createTests(clazz);
    long passedCount = tests.stream().map(TestsRunner::runTest).filter(Boolean::booleanValue).count();
    System.out.printf("Total = %d. Passed = %d. Failed = %d.", tests.size(), passedCount, tests.size() - passedCount);
  }

  private static <T> List<SingleTest<T>> createTests(Class<T> clazz) {
    ClassInfo<T> classInfo = getClassInfo(clazz);
    return classInfo.tests().stream()
        .map(test -> new SingleTest<>(ReflectionHelper.instantiate(classInfo.clazz()), classInfo.before(), test, classInfo.after()))
        .toList();
  }

  private static <T> ClassInfo<T> getClassInfo(Class<T> clazz) {
    Method before = null;
    List<Method> tests = new ArrayList<>();
    Method after = null;

    for (Method method : clazz.getDeclaredMethods()) {
      if (method.isAnnotationPresent(Before.class)) {
        if (before != null) {
          throw new RuntimeException("Only one @Before annotation is possible");
        }
        before = method;
      }
      if (method.isAnnotationPresent(Test.class)) {
        tests.add(method);
      }
      if (method.isAnnotationPresent(After.class)) {
        if (after != null) {
          throw new RuntimeException("Only one @After annotation is possible");
        }
        after = method;
      }
    }
    return new ClassInfo<>(clazz, Optional.ofNullable(before), tests, Optional.ofNullable(after));
  }

  private static <T> boolean runTest(SingleTest<T> test) {
    test.before().ifPresent(before -> {
      try {
        before.invoke(test.instance());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });

    boolean passed = true;
    try {
      test.test().invoke(test.instance());
    } catch (Exception e) {
      passed = false;
    }

    test.after().ifPresent(after -> {
      try {
        after.invoke(test.instance());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });

    return passed;
  }
}
