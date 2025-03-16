package com.yawl.repository;

import com.yawl.exception.MemoryDbDuplicateKeyException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class InMemoryDatabase<KEY, OBJ> {
    private final Map<KEY, OBJ> memoryDb = new ConcurrentHashMap<>();

    public void store(KEY key, OBJ obj) {
        if (memoryDb.containsKey(key)) {
            throw MemoryDbDuplicateKeyException.forKey(key);
        }

        memoryDb.put(key, obj);
    }

    public OBJ get(KEY key) {
        return memoryDb.get(key);
    }

    public void update(KEY key, OBJ obj) {
        memoryDb.put(key, obj);
    }

    public void delete(KEY key) {
        memoryDb.remove(key);
    }
}
