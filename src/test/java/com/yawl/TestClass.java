package com.yawl;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class TestClass {
    private String var1;
    private int var2;
    private boolean var3;
    private double var4;

    public TestClass() {
    }

    public TestClass(String var1, int var2) {
        this.var1 = var1;
        this.var2 = var2;
    }

    public TestClass(String var1, int var2, boolean var3, double var4) {
        this.var1 = var1;
        this.var2 = var2;
        this.var3 = var3;
        this.var4 = var4;
    }

    public void doTheDo() {
        // do some do
    }

    @Override
    public String toString() {
        return "TestClass{" +
                "var1='" + var1 + '\'' +
                ", var2=" + var2 +
                ", var3=" + var3 +
                ", var4=" + var4 +
                '}';
    }
}
