package edu.utsa.cs3443.sweatware_alpha.services;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for managing CSV file operations in the Sweatware application.
 * <p>
 * Provides methods to read, append, and overwrite CSV files. This class is
 * used by controllers and models to persist user and workout data.
 * </p>
 *
 * <p>All methods are static, allowing direct access without instantiation.</p>
 *
 * @author Aiden Gravett
 * @version final
 */
public class DataManager {

    /**
     * Reads a CSV file and returns its contents as a list of string arrays.
     * <p>
     * Each row is split by commas into a {@code String[]} and added to the list.
     * Empty lines are ignored.
     * </p>
     * @param path the path to the CSV file
     * @return a list of rows, where each row is represented as a string array
     */
    public static List<String[]> readCSV(String path) {
        List<String[]> rows = new ArrayList<>();
        File file = new File(path);

        System.out.println("Attempting to read file: " + file.getAbsolutePath());

        if (!file.exists()) {
            System.err.println("File not found: " + path);
            return rows;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    rows.add(line.split(","));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV: " + e.getMessage());
        }
        return rows;
    }

    /**
     * Appends a single row to a CSV file.
     * <p>
     * If the file does not exist, it is created along with any necessary
     * parent directories.
     * </p>
     * @param path   the path to the CSV file
     * @param values the values to append as a new row
     */
    public static void appendToCSV(String path, String... values) {
        File file = new File(path);
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.write(String.join(",", values));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to CSV: " + e.getMessage());
        }
    }

    /**
     * Overwrites a CSV file with new data.
     * <p>
     * Each row in the provided list is written to the file, replacing
     * any existing content.
     * </p>
     * @param path the path to the CSV file
     * @param rows the new data to write, where each row is a string array
     */
    public static void writeCSV(String path, List<String[]> rows) {
        File file = new File(path);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String[] row : rows) {
                writer.write(String.join(",", row));
                writer.newLine();
            }
            System.out.println("CSV successfully updated.");
        } catch (IOException e) {
            System.err.println("Error overwriting CSV: " + e.getMessage());
        }
    }
}