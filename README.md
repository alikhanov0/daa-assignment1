# DAA Assignment 1

Java implementation and evaluation of MergeSort, QuickSort and QuickSelect.

## Requirements

- Java 17
- Maven 3.9+

## Commands

Run all correctness and edge-case tests:

```bash
mvn test
```

Run the five-repeat benchmark, write `results.csv`, and generate the PNG plots:

```bash
mvn exec:java
```

The benchmark uses sizes 1,000, 10,000, 100,000 and 1,000,000 across random, sorted and duplicate-heavy inputs. It records median runtime, comparisons and maximum recursion depth.

## Design

- MergeSort allocates one top-level buffer and switches to insertion sort at 15 elements.
- QuickSort uses a random-pivot, three-way partition and recurses only on the smaller partition.
- QuickSelect uses the same three-way partition and continues iteratively only in the partition containing `k`.
- `Metrics` is passed explicitly to algorithms and contains comparison, depth and elapsed-time data.
