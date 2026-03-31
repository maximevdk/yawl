package com.yawl.exception;

/**
 * Thrown when an attempt is made to store an entry with a key that already exists in the in-memory database.
 */
public class MemoryDbDuplicateKeyException extends RuntimeException {
    private static final String MESSAGE = "Duplicate key [%s] detected in MemoryDB";

    private MemoryDbDuplicateKeyException(String message) {
        super(message);
    }

    /**
     * Creates an exception for a duplicate key.
     *
     * @param key the duplicate key
     * @return a new exception instance
     */
    public static MemoryDbDuplicateKeyException forKey(Object key) {
        return new MemoryDbDuplicateKeyException(MESSAGE.formatted(key));
    }
}
