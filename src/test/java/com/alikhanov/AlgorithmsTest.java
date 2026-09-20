package com.alikhanov;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlgorithmsTest {
    @Test
    void sortsMatchJdkForRandomArrays() {
        Random random = new Random(42);
        for (int run = 0; run < 100; run++) {
            int[] input = randomArray(random, random.nextInt(500));
            int[] expected = Arrays.copyOf(input, input.length);
            Arrays.sort(expected);
            int[] mergeActual = Arrays.copyOf(input, input.length);
            int[] quickActual = Arrays.copyOf(input, input.length);
            Algorithms.mergeSort(mergeActual, new Metrics());
            Algorithms.quickSort(quickActual, new Metrics());
            assertArrayEquals(expected, mergeActual);
            assertArrayEquals(expected, quickActual);
        }
    }

    @Test
    void sortsHandleRequiredEdgeCases() {
        int[][] cases = {{}, {7}, {4, 4, 4, 4}, {1, 2, 3, 4, 5}};
        for (int[] input : cases) {
            int[] expected = Arrays.copyOf(input, input.length);
            Arrays.sort(expected);
            int[] mergeActual = Arrays.copyOf(input, input.length);
            int[] quickActual = Arrays.copyOf(input, input.length);
            Algorithms.mergeSort(mergeActual, new Metrics());
            Algorithms.quickSort(quickActual, new Metrics());
            assertArrayEquals(expected, mergeActual);
            assertArrayEquals(expected, quickActual);
        }
    }

    @Test
    void quickSortDepthIsBoundedOnSortedInput() {
        int size = 100_000;
        int[] input = new int[size];
        for (int index = 0; index < size; index++) {
            input[index] = index;
        }
        Metrics metrics = new Metrics();
        Algorithms.quickSort(input, metrics);
        assertTrue(metrics.maxDepth() <= 2 * (Math.log(size) / Math.log(2)));
    }

    @Test
    void quickSelectMatchesSortedValues() {
        Random random = new Random(7);
        for (int run = 0; run < 100; run++) {
            int[] input = randomArray(random, random.nextInt(500) + 1);
            int[] sorted = Arrays.copyOf(input, input.length);
            Arrays.sort(sorted);
            int k = random.nextInt(input.length);
            assertEquals(sorted[k], Algorithms.select(input, k, new Metrics()));
        }
    }

    @Test
    void quickSelectRejectsInvalidArguments() {
        assertThrows(IllegalArgumentException.class, () -> Algorithms.select(new int[0], 0, new Metrics()));
        assertThrows(IllegalArgumentException.class, () -> Algorithms.select(new int[]{1, 2}, -1, new Metrics()));
        assertThrows(IllegalArgumentException.class, () -> Algorithms.select(new int[]{1, 2}, 2, new Metrics()));
    }

    private int[] randomArray(Random random, int size) {
        int[] values = new int[size];
        for (int index = 0; index < size; index++) {
            values[index] = random.nextInt();
        }
        return values;
    }
}
