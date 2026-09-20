package com.alikhanov;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public final class Benchmark {
    private static final int[] SIZES = {1_000, 10_000, 100_000, 1_000_000};
    private static final String[] INPUTS = {"random", "sorted", "duplicates"};
    private static final String[] ALGORITHMS = {"MergeSort", "QuickSort", "QuickSelect"};
    private static final int RUNS = 5;

    private Benchmark() {
    }

    public static void main(String[] args) throws IOException {
        List<Result> results = new ArrayList<>();
        Random random = new Random(20260920L);
        for (String input : INPUTS) {
            for (int size : SIZES) {
                int[] source = createInput(size, input, random);
                for (String algorithm : ALGORITHMS) {
                    results.add(runMedian(algorithm, input, source));
                }
            }
        }
        writeCsv(results, Path.of("results.csv"));
        Plotter.writePlots(results, Path.of("plots"));
        System.out.println("Created results.csv and plots in " + Path.of("plots").toAbsolutePath());
    }

    private static int[] createInput(int size, String input, Random random) {
        int[] values = new int[size];
        for (int index = 0; index < size; index++) {
            values[index] = input.equals("duplicates") ? random.nextInt(10) : random.nextInt();
        }
        if (input.equals("sorted")) {
            Arrays.sort(values);
        }
        return values;
    }

    private static Result runMedian(String algorithm, String input, int[] source) {
        List<Result> runs = new ArrayList<>();
        for (int run = 0; run < RUNS; run++) {
            int[] values = Arrays.copyOf(source, source.length);
            Metrics metrics = new Metrics();
            metrics.startTimer();
            if (algorithm.equals("MergeSort")) {
                Algorithms.mergeSort(values, metrics);
            } else if (algorithm.equals("QuickSort")) {
                Algorithms.quickSort(values, metrics);
            } else {
                Algorithms.select(values, values.length / 2, metrics);
            }
            metrics.stopTimer();
            runs.add(new Result(algorithm, input, source.length, metrics.timeMillis(), metrics.comparisons(), metrics.maxDepth()));
        }
        runs.sort(Comparator.comparingDouble(Result::timeMs));
        return runs.get(RUNS / 2);
    }

    private static void writeCsv(List<Result> results, Path file) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            writer.write("algorithm,input,n,time_ms,comparisons,max_depth\n");
            for (Result result : results) {
                writer.write(String.format(Locale.US, "%s,%s,%d,%.4f,%d,%d%n", result.algorithm(), result.input(), result.size(), result.timeMs(), result.comparisons(), result.maxDepth()));
            }
        }
    }

    public record Result(String algorithm, String input, int size, double timeMs, long comparisons, int maxDepth) {
    }
}
