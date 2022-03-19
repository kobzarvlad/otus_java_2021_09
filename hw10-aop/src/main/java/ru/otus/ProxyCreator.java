package ru.otus;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;

public class ProxyCreator {

  private ProxyCreator() {
  }

  public static Object create(Object object) {
    InvocationHandler handler = new LogInvocationHandler(object);
    return Proxy.newProxyInstance(ProxyCreator.class.getClassLoader(), object.getClass().getInterfaces(), handler);
  }

  private static class LogInvocationHandler implements InvocationHandler {

    private final Object object;
    private final List<Method> methodsToLog;

    LogInvocationHandler(Object object) {
      this.object = object;
      this.methodsToLog = Arrays.stream(object.getClass().getDeclaredMethods())
          .filter(m -> m.isAnnotationPresent(Log.class))
          .toList();
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
      if (methodsToLog.stream().anyMatch(m -> m.getName().equals(method.getName()) && Arrays.equals(m.getParameterTypes(), method.getParameterTypes()))) {
        System.out.printf("executed method: %s, params: %s%n", method.getName(), objects != null ? Arrays.toString(objects) : List.of());
      }
      return method.invoke(object, objects);
    }
  }
}
