import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * MP17 - Find the longest text entry in dataset.
 * Student: CELADEZ, JED CEDRIC G.
 */
public class MP17 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter dataset file path: ");
        String filePath = scanner.nextLine().trim();

        List<String> headers = new ArrayList<>();
        List<List<String>> rows = new ArrayList<>();

        try {
            loadDataset(filePath, headers, rows);
            runMP17(headers, rows);
        } catch (IOException e) {
            System.out.println("Error: Unable to read file. " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    private static void runMP17(List<String> headers, List<List<String>> rows) {
        String longestEntry = "";
        int longestRow = -1;
        int longestCol = -1;

        for (int r = 0; r < rows.size(); r++) {
            List<String> row = rows.get(r);
            for (int c = 0; c < row.size(); c++) {
                String value = row.get(c).trim();
                if (value.length() > longestEntry.length()) {
                    longestEntry = value;
                    longestRow = r;
                    longestCol = c;
                }
            }
        }

        System.out.println("\n--- Longest Text Entry Found ---");
        if (longestRow == -1) {
            System.out.println("No valid data found.");
            return;
        }

        String columnName = (longestCol >= 0 && longestCol < headers.size())
                ? headers.get(longestCol)
                : "Column " + (longestCol + 1);

        System.out.println("Text: " + longestEntry);
        System.out.println("Length: " + longestEntry.length() + " characters");
        System.out.println("Location: Row " + (longestRow + 1) + ", Column: " + columnName);
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
