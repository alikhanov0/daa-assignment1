package com.alikhanov;

import java.util.concurrent.ThreadLocalRandom;

public final class Algorithms {
    private static final int CUTOFF = 15;

    private Algorithms() {
    }

    public static void mergeSort(int[] values, Metrics metrics) {
        if (values == null) {
            throw new IllegalArgumentException("array must not be null");
        }
        if (values.length < 2) {
            metrics.recordDepth(values.length == 0 ? 0 : 1);
            return;
        }
        int[] buffer = new int[values.length];
        mergeSort(values, buffer, 0, values.length - 1, 1, metrics);
    }

    private static void mergeSort(int[] values, int[] buffer, int low, int high, int depth, Metrics metrics) {
        metrics.recordDepth(depth);
        if (high - low + 1 <= CUTOFF) {
            insertionSort(values, low, high, metrics);
            return;
        }
        int middle = low + (high - low) / 2;
        mergeSort(values, buffer, low, middle, depth + 1, metrics);
        mergeSort(values, buffer, middle + 1, high, depth + 1, metrics);
        merge(values, buffer, low, middle, high, metrics);
    }

    private static void merge(int[] values, int[] buffer, int low, int middle, int high, Metrics metrics) {
        System.arraycopy(values, low, buffer, low, high - low + 1);
        int left = low;
        int right = middle + 1;
        for (int index = low; index <= high; index++) {
            if (left > middle) {
                values[index] = buffer[right++];
            } else if (right > high) {
                values[index] = buffer[left++];
            } else {
                metrics.comparison();
                if (buffer[left] <= buffer[right]) {
                    values[index] = buffer[left++];
                } else {
                    values[index] = buffer[right++];
                }
            }
        }
    }

    private static void insertionSort(int[] values, int low, int high, Metrics metrics) {
        for (int index = low + 1; index <= high; index++) {
            int value = values[index];
            int position = index - 1;
            while (position >= low) {
                metrics.comparison();
                if (values[position] <= value) {
                    break;
                }
                values[position + 1] = values[position];
                position--;
            }
            values[position + 1] = value;
        }
    }

    public static void quickSort(int[] values, Metrics metrics) {
        if (values == null) {
            throw new IllegalArgumentException("array must not be null");
        }
        quickSort(values, 0, values.length - 1, 1, metrics);
    }

    private static void quickSort(int[] values, int low, int high, int depth, Metrics metrics) {
        while (low < high) {
            metrics.recordDepth(depth);
            int[] equalRange = partition(values, low, high, metrics);
            int leftSize = equalRange[0] - low;
            int rightSize = high - equalRange[1];
            if (leftSize < rightSize) {
                quickSort(values, low, equalRange[0] - 1, depth + 1, metrics);
                low = equalRange[1] + 1;
            } else {
                quickSort(values, equalRange[1] + 1, high, depth + 1, metrics);
                high = equalRange[0] - 1;
            }
        }
        if (low == high) {
            metrics.recordDepth(depth);
        }
    }

    public static int select(int[] values, int k, Metrics metrics) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("array must not be null or empty");
        }
        if (k < 0 || k >= values.length) {
            throw new IllegalArgumentException("k must be between 0 and " + (values.length - 1));
        }
        int low = 0;
        int high = values.length - 1;
        metrics.recordDepth(1);
        while (low <= high) {
            int[] equalRange = partition(values, low, high, metrics);
            if (k < equalRange[0]) {
                high = equalRange[0] - 1;
            } else if (k > equalRange[1]) {
                low = equalRange[1] + 1;
            } else {
                return values[k];
            }
        }
        throw new IllegalStateException("selection failed");
    }

    private static int[] partition(int[] values, int low, int high, Metrics metrics) {
        int pivot = values[ThreadLocalRandom.current().nextInt(low, high + 1)];
        int less = low;
        int index = low;
        int greater = high;
        while (index <= greater) {
            metrics.comparison();
            if (values[index] < pivot) {
                swap(values, less++, index++);
            } else {
                metrics.comparison();
                if (values[index] > pivot) {
                    swap(values, index, greater--);
                } else {
                    index++;
                }
            }
        }
        return new int[]{less, greater};
    }

    private static void swap(int[] values, int first, int second) {
        int temporary = values[first];
        values[first] = values[second];
        values[second] = temporary;
    }
}
