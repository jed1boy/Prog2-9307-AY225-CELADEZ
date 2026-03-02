import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class MonthlyPerformanceAnalyzer {

    private static final String UNKNOWN_MONTH = "Unknown";
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM", Locale.ROOT);

    private static class AnalysisResult {
        List<Map.Entry<String, Double>> sortedMonthly;
        String bestMonth;
        double bestSales;
        int processedRows;
        int skippedRows;
    }

    private static class DataRecord {
        private final String monthKey;
        private final double totalSales;

        DataRecord(String monthKey, double totalSales) {
            this.monthKey = monthKey;
            this.totalSales = totalSales;
        }

        String getMonthKey() {
            return monthKey;
        }

        double getTotalSales() {
            return totalSales;
        }
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        File datasetFile;

        while (true) {
            System.out.print("Enter dataset file path: ");
            String path = input.nextLine().trim();
            datasetFile = new File(path);

            String validationError = validateCsvFile(datasetFile);
            if (validationError == null) {
                break;
            }

            System.out.println("Invalid file path or CSV: " + validationError);
        }

        try {
            System.out.println("File found and validated. Processing...");
            AnalysisResult result = analyzeMonthlySales(datasetFile);
            printSummary(result);
        } catch (IOException e) {
            System.out.println("An error occurred while processing the dataset: " + e.getMessage());
        } finally {
            input.close();
        }
    }

    private static String validateCsvFile(File file) {
        if (!file.exists()) {
            return "File does not exist.";
        }

        if (!file.isFile()) {
            return "Path is not a file.";
        }

        if (!file.canRead()) {
            return "File is not readable.";
        }

        String name = file.getName().toLowerCase(Locale.ROOT);
        if (!name.endsWith(".csv")) {
            return "File must have a .csv extension.";
        }

        String headerLine = null;
        String firstDataLine = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                if (headerLine == null) {
                    headerLine = line;
                } else {
                    firstDataLine = line;
                    break;
                }
            }
        } catch (IOException e) {
            return "Failed to read file content.";
        }

        if (headerLine == null || firstDataLine == null) {
            return "CSV must include a header and at least one data row.";
        }

        List<String> headers = normalizeHeader(parseCsvLine(headerLine));
        if (!headers.contains("total_sales")) {
            return "CSV missing required column: total_sales";
        }
        if (!headers.contains("release_date")) {
            return "CSV missing required column: release_date";
        }

        return null;
    }

    private static AnalysisResult analyzeMonthlySales(File file) throws IOException {
        List<String> dataLines = new ArrayList<>();
        String headerLine = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                if (headerLine == null) {
                    headerLine = line;
                } else {
                    dataLines.add(line);
                }
            }
        }

        if (headerLine == null) {
            throw new IOException("CSV header is missing.");
        }

        List<String> headerCells = normalizeHeader(parseCsvLine(headerLine));
        int totalSalesIndex = headerCells.indexOf("total_sales");
        int releaseDateIndex = headerCells.indexOf("release_date");
        int naSalesIndex = headerCells.indexOf("na_sales");
        int jpSalesIndex = headerCells.indexOf("jp_sales");
        int palSalesIndex = headerCells.indexOf("pal_sales");
        int otherSalesIndex = headerCells.indexOf("other_sales");

        Map<String, Double> monthlyTotals = new LinkedHashMap<>();
        int processedRows = 0;
        int skippedRows = 0;

        int requiredMaxIndex = Math.max(totalSalesIndex, releaseDateIndex);

        for (String dataLine : dataLines) {
            List<String> cells = parseCsvLine(dataLine);

            if (cells.size() <= requiredMaxIndex) {
                skippedRows++;
                continue;
            }

            String monthKey = toMonthKey(cells.get(releaseDateIndex));
            if (monthKey == null) {
                monthKey = UNKNOWN_MONTH;
            }

            double sales = parseDoubleOrNaN(safeCell(cells, totalSalesIndex));
            if (Double.isNaN(sales)) {
                double na = parseDoubleOrZero(safeCell(cells, naSalesIndex));
                double jp = parseDoubleOrZero(safeCell(cells, jpSalesIndex));
                double pal = parseDoubleOrZero(safeCell(cells, palSalesIndex));
                double other = parseDoubleOrZero(safeCell(cells, otherSalesIndex));
                sales = na + jp + pal + other;
            }

            DataRecord record = new DataRecord(monthKey, sales);
            monthlyTotals.put(record.getMonthKey(), monthlyTotals.getOrDefault(record.getMonthKey(), 0.0) + record.getTotalSales());
            processedRows++;
        }

        List<Map.Entry<String, Double>> sortedMonthly = new ArrayList<>(monthlyTotals.entrySet());
        sortedMonthly.sort(new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> a, Map.Entry<String, Double> b) {
                boolean aUnknown = UNKNOWN_MONTH.equals(a.getKey());
                boolean bUnknown = UNKNOWN_MONTH.equals(b.getKey());

                if (aUnknown && !bUnknown) return 1;
                if (!aUnknown && bUnknown) return -1;
                return a.getKey().compareTo(b.getKey());
            }
        });

        String bestMonth = null;
        double bestSales = 0.0;
        for (Map.Entry<String, Double> row : sortedMonthly) {
            if (bestMonth == null || row.getValue() > bestSales) {
                bestMonth = row.getKey();
                bestSales = row.getValue();
            }
        }

        AnalysisResult result = new AnalysisResult();
        result.sortedMonthly = sortedMonthly;
        result.bestMonth = bestMonth;
        result.bestSales = bestSales;
        result.processedRows = processedRows;
        result.skippedRows = skippedRows;
        return result;
    }

    private static void printSummary(AnalysisResult result) {
        System.out.println("\n=== Monthly Performance Summary ===");

        if (result.sortedMonthly.isEmpty()) {
            System.out.println("No valid rows found for monthly computation.");
            return;
        }

        System.out.println("Month     | Total Sales");
        System.out.println("------------------------");

        for (Map.Entry<String, Double> row : result.sortedMonthly) {
            System.out.printf(Locale.ROOT, "%s | %.2f%n", row.getKey(), row.getValue());
        }

        System.out.println("------------------------");
        System.out.printf(Locale.ROOT, "Best-performing month: %s (%.2f)%n", result.bestMonth, result.bestSales);
        System.out.println("Processed rows: " + result.processedRows);
        System.out.println("Skipped rows: " + result.skippedRows);
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

    private static List<String> normalizeHeader(List<String> headers) {
        List<String> normalized = new ArrayList<>(headers.size());
        for (String header : headers) {
            normalized.add(header.trim().toLowerCase(Locale.ROOT));
        }
        return normalized;
    }

    private static String toMonthKey(String dateText) {
        if (dateText == null) {
            return null;
        }

        String raw = dateText.trim();
        if (raw.isEmpty()) {
            return null;
        }

        String[] parts = raw.split("-");
        if (parts.length != 3) {
            return null;
        }

        String year = parts[0].trim();
        String month = parts[1].trim();

        if (!year.matches("\\d{4}") || !month.matches("\\d{1,2}")) {
            return null;
        }

        int monthValue;
        try {
            monthValue = Integer.parseInt(month);
        } catch (NumberFormatException e) {
            return null;
        }

        if (monthValue < 1 || monthValue > 12) {
            return null;
        }

        try {
            return YearMonth.of(Integer.parseInt(year), monthValue).format(MONTH_FORMATTER);
        } catch (DateTimeParseException | NumberFormatException e) {
            return null;
        }
    }

    private static String safeCell(List<String> cells, int index) {
        if (index < 0 || index >= cells.size()) {
            return "";
        }
        return cells.get(index);
    }

    private static double parseDoubleOrNaN(String value) {
        if (value == null) {
            return Double.NaN;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }

    private static double parseDoubleOrZero(String value) {
        double n = parseDoubleOrNaN(value);
        return Double.isNaN(n) ? 0.0 : n;
    }
}
