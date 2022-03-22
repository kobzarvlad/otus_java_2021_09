package ru.otus.cachehw;


import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyCache<K, V> implements HwCache<K, V> {

    private final Logger log = LoggerFactory.getLogger(MyCache.class);

    private final Set<HwListener<K, V>> listeners = new HashSet<>();

    private final Map<K, V> cache = new WeakHashMap<>();
    private static final String PUT_ACTION = "put";
    private static final String REMOVE_ACTION = "remove";
    private static final String GET_ACTION = "get";

    //Надо реализовать эти методы
    @Override
    public void put(K key, V value) {
        cache.put(key, value);
        invokeListeners(key, value, PUT_ACTION);
    }

    @Override
    public void remove(K key) {
        V removedValue = cache.remove(key);
        invokeListeners(key, removedValue, REMOVE_ACTION);
    }

    @Override
    public V get(K key) {
        V value = cache.get(key);
        invokeListeners(key, value, GET_ACTION);
        return value;
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        listeners.remove(listener);
    }

    private void invokeListeners(K key, V value, String action) {
        try {
            listeners.forEach(listener -> listener.notify(key, value, action));
        } catch (Exception e) {
            log.warn("Smth went wrong during listener notification.", e);
        }
    }
}
