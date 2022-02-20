package ru.otus.lib;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import ru.otus.annotations.After;
import ru.otus.annotations.Before;
import ru.otus.annotations.Test;
import static ru.otus.lib.TestResult.FAILED;
import static ru.otus.lib.TestResult.PASSED;
import static ru.otus.lib.TestResult.SKIPPED;

public class TestsRunner {

  public static <T> void runTests(Class<T> clazz) {
    List<SingleTest> tests = createTests(clazz);
    Map<TestResult, Long> resultMap = tests.stream()
        .map(TestsRunner::runTest)
        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

    System.out.printf("Total = %d. Passed = %d. Skipped = %d. Failed = %d.%n",
        tests.size(), resultMap.getOrDefault(PASSED, 0L), resultMap.getOrDefault(SKIPPED, 0L), resultMap.getOrDefault(FAILED, 0L));
  }

  private static <T> List<SingleTest> createTests(Class<T> clazz) {
    ClassInfo<T> classInfo = getClassInfo(clazz);
    return classInfo.tests().stream()
        .map(test -> new SingleTest(ReflectionHelper.instantiate(classInfo.clazz()), classInfo.before(), test, classInfo.after()))
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
    return new ClassInfo<>(clazz, before, tests, after);
  }

  private static <T> TestResult runTest(SingleTest test) {
    TestResult result;

    if (invokeMethod(test.before(), test.instance()) == PASSED) {
      result = invokeMethod(test.test(), test.instance());
    } else {
      result = SKIPPED;
    }
    invokeMethod(test.after(), test.instance());

    return result;
  }

  private static <T> TestResult invokeMethod(Method method, Object instance) {
    if (method != null) {
      try {
        method.invoke(instance);
      } catch (Exception e) {
        e.printStackTrace();
        return FAILED;
      }
    }
    return PASSED;
  }
}
