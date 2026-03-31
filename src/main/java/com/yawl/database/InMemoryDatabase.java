package com.yawl.database;

import com.yawl.exception.MemoryDbDuplicateKeyException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * Abstract base class for a simple in-memory key-value store backed by a {@link java.util.concurrent.ConcurrentHashMap}.
 *
 * @param <KEY> the key type
 * @param <OBJ> the value type
 */
public abstract class InMemoryDatabase<KEY, OBJ> {
    private final Map<KEY, OBJ> memoryDb = new ConcurrentHashMap<>();

    /**
     * Stores an entry. Throws if the key already exists.
     *
     * @param key the key
     * @param obj the value
     */
    public void store(KEY key, OBJ obj) {
        if (memoryDb.containsKey(key)) {
            throw MemoryDbDuplicateKeyException.forKey(key);
        }

        memoryDb.put(key, obj);
    }

    /**
     * Returns the value associated with the given key, or {@code null} if not found.
     *
     * @param key the key
     * @return the value, or {@code null}
     */
    public OBJ get(KEY key) {
        return memoryDb.get(key);
    }

    /**
     * Inserts or replaces the value for the given key.
     *
     * @param key the key
     * @param obj the value
     */
    public void update(KEY key, OBJ obj) {
        memoryDb.put(key, obj);
    }

    /**
     * Removes the entry with the given key.
     *
     * @param key the key
     */
    public void delete(KEY key) {
        memoryDb.remove(key);
    }

    /**
     * Returns all values matching the given predicate.
     *
     * @param predicate the filter
     * @return a list of matching values
     */
    public List<OBJ> find(Predicate<OBJ> predicate) {
        return memoryDb.values()
                .stream()
                .filter(predicate)
                .toList();
    }
}
