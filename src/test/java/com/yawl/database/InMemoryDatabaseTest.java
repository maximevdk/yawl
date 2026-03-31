package com.yawl.database;

import com.yawl.TestClass;
import com.yawl.exception.MemoryDbDuplicateKeyException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryDatabaseTest {
    private final InMemoryDatabase<Integer, TestClass> db = new InMemoryDatabase<>() {
    };
    private final TestClass input = new TestClass("1", 1, true, 1.0);

    @Test
    void it() {
        db.store(1, input);

        assertEquals(input, db.get(1));
        assertEquals(List.of(input), db.find(testClass -> testClass.equals(input)));

        var update = new TestClass("2", 2, false, 2.0);
        db.update(1, update);
        assertEquals(update, db.get(1));
        assertEquals(List.of(), db.find(testClass -> testClass.equals(input)));

        assertThrows(MemoryDbDuplicateKeyException.class, () -> db.store(1, new TestClass()));

        db.delete(1);
        assertNull(db.get(1));
        assertEquals(List.of(), db.find(testClass -> testClass.equals(update)));
    }
}