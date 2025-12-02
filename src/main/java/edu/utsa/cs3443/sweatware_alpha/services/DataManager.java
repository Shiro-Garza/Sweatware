package edu.utsa.cs3443.sweatware_alpha.services;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataManager {

    // Reads a CSV file and returns a list of string arrays (rows)
    public static List<String[]> readCSV(String path) {
        List<String[]> rows = new ArrayList<>();
        File file = new File(path);

        // Debugging output to help trace file issues
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

    // Appends a single row to a CSV file
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

    // Overwrites a CSV file with new data
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