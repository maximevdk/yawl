package com.yawl.exception;

public class MemoryDbDuplicateKeyException extends RuntimeException {
    private static final String MESSAGE = "Duplicate key [%s] detected in MemoryDB";


    private MemoryDbDuplicateKeyException(String message) {
        super(message);
    }

    public static MemoryDbDuplicateKeyException forKey(Object key) {
        return new MemoryDbDuplicateKeyException(MESSAGE.formatted(key));
    }
}
