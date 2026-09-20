package com.alikhanov;

public class Main {
    public static void main(String[] args) {
        try {
            Benchmark.main(args);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
