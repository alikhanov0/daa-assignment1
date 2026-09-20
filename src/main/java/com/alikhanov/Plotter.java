package com.alikhanov;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class Plotter {
    private static final Color[] COLORS = {new Color(31, 119, 180), new Color(255, 127, 14), new Color(44, 160, 44), new Color(214, 39, 40), new Color(148, 103, 189), new Color(140, 86, 75), new Color(227, 119, 194), new Color(127, 127, 127), new Color(23, 190, 207)};

    private Plotter() {
    }

    public static void writePlots(List<Benchmark.Result> results, Path directory) throws IOException {
        Files.createDirectories(directory);
        draw(results, directory.resolve("time_vs_n.png"), "Time vs n", "milliseconds", Mode.TIME);
        draw(results, directory.resolve("depth_vs_n.png"), "Maximum recursion depth vs n", "depth", Mode.DEPTH);
        draw(results, directory.resolve("ratio_vs_n.png"), "Comparison-growth ratio vs n", "comparisons / growth", Mode.RATIO);
    }

    private static void draw(List<Benchmark.Result> results, Path file, String title, String yLabel, Mode mode) throws IOException {
        int width = 1200;
        int height = 760;
        int left = 100;
        int right = 290;
        int top = 75;
        int bottom = 100;
        double maximum = results.stream().mapToDouble(result -> value(result, mode)).max().orElse(1);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);
        graphics.setColor(new Color(40, 40, 40));
        graphics.setFont(new Font("SansSerif", Font.BOLD, 22));
        graphics.drawString(title, left, 42);
        graphics.setFont(new Font("SansSerif", Font.PLAIN, 14));
        for (int tick = 0; tick <= 5; tick++) {
            int y = height - bottom - tick * (height - top - bottom) / 5;
            graphics.setColor(new Color(225, 225, 225));
            graphics.drawLine(left, y, width - right, y);
            graphics.setColor(new Color(70, 70, 70));
            graphics.drawString(String.format("%.2f", maximum * tick / 5), 18, y + 5);
        }
        graphics.setColor(Color.DARK_GRAY);
        graphics.drawLine(left, top, left, height - bottom);
        graphics.drawLine(left, height - bottom, width - right, height - bottom);
        int[] sizes = {1_000, 10_000, 100_000, 1_000_000};
        for (int index = 0; index < sizes.length; index++) {
            int x = x(index, sizes.length, left, width - right);
            graphics.drawString(String.format("%,d", sizes[index]), x - 20, height - bottom + 25);
        }
        graphics.drawString("n", (left + width - right) / 2, height - 35);
        graphics.drawString(yLabel, 18, top - 15);
        int colorIndex = 0;
        for (String algorithm : new String[]{"MergeSort", "QuickSort", "QuickSelect"}) {
            for (String input : new String[]{"random", "sorted", "duplicates"}) {
                Color color = COLORS[colorIndex++];
                graphics.setColor(color);
                graphics.setStroke(new BasicStroke(2.2f));
                int previousX = -1;
                int previousY = -1;
                for (int index = 0; index < sizes.length; index++) {
                    Benchmark.Result result = find(results, algorithm, input, sizes[index]);
                    int x = x(index, sizes.length, left, width - right);
                    int y = (int) (height - bottom - value(result, mode) / maximum * (height - top - bottom));
                    if (previousX >= 0) {
                        graphics.drawLine(previousX, previousY, x, y);
                    }
                    graphics.fillOval(x - 4, y - 4, 8, 8);
                    previousX = x;
                    previousY = y;
                }
                graphics.drawString(algorithm + " / " + input, width - right + 18, top + (colorIndex - 1) * 27);
            }
        }
        graphics.dispose();
        ImageIO.write(image, "png", file.toFile());
    }

    private static int x(int index, int count, int left, int right) {
        return left + index * (right - left) / (count - 1);
    }

    private static Benchmark.Result find(List<Benchmark.Result> results, String algorithm, String input, int size) {
        return results.stream().filter(result -> result.algorithm().equals(algorithm) && result.input().equals(input) && result.size() == size).findFirst().orElseThrow();
    }

    private static double value(Benchmark.Result result, Mode mode) {
        return switch (mode) {
            case TIME -> result.timeMs();
            case DEPTH -> result.maxDepth();
            case RATIO -> result.algorithm().equals("QuickSelect") ? result.comparisons() / (double) result.size() : result.comparisons() / (result.size() * log2(result.size()));
        };
    }

    private static double log2(int value) {
        return Math.log(value) / Math.log(2);
    }

    private enum Mode { TIME, DEPTH, RATIO }
}
