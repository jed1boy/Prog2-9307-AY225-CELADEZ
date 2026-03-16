import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * MP18 - Remove rows with empty fields.
 * Student: CELADEZ, JED CEDRIC G.
 */
public class MP18 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter dataset file path: ");
        String filePath = scanner.nextLine().trim();

        List<String> headers = new ArrayList<>();
        List<List<String>> rows = new ArrayList<>();

        try {
            loadDataset(filePath, headers, rows);
            runMP18(rows);
        } catch (IOException e) {
            System.out.println("Error: Unable to read file. " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    private static void runMP18(List<List<String>> rows) {
        List<List<String>> cleanedRows = new ArrayList<>();
        int rowsRemoved = 0;

        for (List<String> row : rows) {
            if (hasEmptyField(row)) {
                rowsRemoved++;
            } else {
                cleanedRows.add(row);
            }
        }

        System.out.println("\n--- Dataset Cleanup Summary ---");
        System.out.println("Removed " + rowsRemoved + " rows containing empty fields.");
        System.out.println("Remaining valid records: " + cleanedRows.size());

        System.out.println("\n--- Sample Records (First 5) ---");
        if (cleanedRows.isEmpty()) {
            System.out.println("No rows remain after cleanup.");
            return;
        }

        int sampleCount = Math.min(5, cleanedRows.size());
        for (int i = 0; i < sampleCount; i++) {
            System.out.println(String.join(" | ", cleanedRows.get(i)));
        }
    }

    private static boolean hasEmptyField(List<String> row) {
        for (String value : row) {
            if (value == null || value.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private static void loadDataset(String filePath, List<String> headers, List<List<String>> rows)
            throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean headerFound = false;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                List<String> cells = parseCsvLine(line);

                if (!headerFound) {
                    boolean isHeader = false;
                    for (String cell : cells) {
                        if ("candidate".equalsIgnoreCase(cell.trim())) {
                            isHeader = true;
                            break;
                        }
                    }

                    if (isHeader) {
                        headers.addAll(cells);
                        headerFound = true;
                    }
                    continue;
                }

                rows.add(cells);
            }
        }

        if (headers.isEmpty()) {
            throw new IllegalArgumentException("Dataset header row not found.");
        }
    }

    private static List<String> parseCsvLine(String line) {
        List<String> cells = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);

            if (ch == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (ch == ',' && !inQuotes) {
                cells.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }

        cells.add(current.toString());
        return cells;
    }
}
