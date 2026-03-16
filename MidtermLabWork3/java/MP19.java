import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * MP19 - Generate dataset summary report.
 * Student: CELADEZ, JED CEDRIC G.
 */
public class MP19 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter dataset file path: ");
        String filePath = scanner.nextLine().trim();

        List<String> headers = new ArrayList<>();
        List<List<String>> rows = new ArrayList<>();

        try {
            loadDataset(filePath, headers, rows);
            runMP19(headers, rows);
        } catch (IOException e) {
            System.out.println("Error: Unable to read file. " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    private static void runMP19(List<String> headers, List<List<String>> rows) {
        int totalRecords = rows.size();
        int columnCount = headers.size();

        int examColIndex = findHeaderIndex(headers, "exam");
        int resultColIndex = findHeaderIndex(headers, "result");

        Map<String, Integer> examCounts = new HashMap<>();
        Map<String, Integer> resultCounts = new HashMap<>();

        for (List<String> row : rows) {
            if (examColIndex >= 0 && examColIndex < row.size()) {
                String exam = row.get(examColIndex).trim();
                if (!exam.isEmpty()) {
                    examCounts.put(exam, examCounts.getOrDefault(exam, 0) + 1);
                }
            }

            if (resultColIndex >= 0 && resultColIndex < row.size()) {
                String result = row.get(resultColIndex).trim();
                if (!result.isEmpty()) {
                    resultCounts.put(result, resultCounts.getOrDefault(result, 0) + 1);
                }
            }
        }

        System.out.println("\n========= DATASET SUMMARY REPORT =========");
        System.out.println("Total Records: " + totalRecords);
        System.out.println("Number of Columns: " + columnCount);

        System.out.println("\n--- Pass/Fail Distribution ---");
        if (resultCounts.isEmpty()) {
            System.out.println("No result values found.");
        } else {
            List<Map.Entry<String, Integer>> resultEntries = new ArrayList<>(resultCounts.entrySet());
            resultEntries.sort(Map.Entry.comparingByKey(String.CASE_INSENSITIVE_ORDER));
            for (Map.Entry<String, Integer> entry : resultEntries) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }

        System.out.println("\n--- Most Popular Exams ---");
        if (examCounts.isEmpty()) {
            System.out.println("No exam values found.");
        } else {
            List<Map.Entry<String, Integer>> examEntries = new ArrayList<>(examCounts.entrySet());
            examEntries.sort(Comparator.comparing(Map.Entry<String, Integer>::getValue).reversed());

            int top = Math.min(5, examEntries.size());
            for (int i = 0; i < top; i++) {
                Map.Entry<String, Integer> entry = examEntries.get(i);
                System.out.println(entry.getKey() + ": " + entry.getValue() + " candidates");
            }
        }
    }

    private static int findHeaderIndex(List<String> headers, String target) {
        for (int i = 0; i < headers.size(); i++) {
            if (target.equalsIgnoreCase(headers.get(i).trim())) {
                return i;
            }
        }
        return -1;
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
