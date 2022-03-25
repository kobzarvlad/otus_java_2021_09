package ru.otus.appcontainer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;

public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

    public AppComponentsContainerImpl(Class<?> initialConfigClass) {
        processConfig(initialConfigClass);
    }

    private void processConfig(Class<?> configClass) {
        checkConfigClass(configClass);
        // You code here...
        Object configClassInstance;
        try {
            configClassInstance = configClass.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Arrays.stream(configClass.getDeclaredMethods())
            .filter(method -> method.isAnnotationPresent(AppComponent.class))
            .sorted(Comparator.comparing(method -> method.getAnnotation(AppComponent.class).order()))
            .forEach(method -> initializeAppComponent(configClassInstance, method));
    }

    private void initializeAppComponent(Object configClassInstance, Method method) {
        Object[] params = Arrays.stream(method.getParameterTypes())
            .map(paramType -> Optional.ofNullable(getAppComponent(paramType)).orElseThrow())
            .toArray();
        try {
            Object appComponent = method.invoke(configClassInstance, params);
            appComponents.add(appComponent);
            appComponentsByName.put(method.getAnnotation(AppComponent.class).name(), appComponent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", configClass.getName()));
        }
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public <C> C getAppComponent(Class<C> componentClass) {
        return (C) appComponents.stream()
            .filter(component -> componentClass.isAssignableFrom(component.getClass()))
            .findAny()
            .orElseThrow();
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public <C> C getAppComponent(String componentName) {
        return (C) appComponentsByName.get(componentName);
    }
}
