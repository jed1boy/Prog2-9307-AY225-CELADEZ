package Java;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PrelimGradeCalculator extends JFrame {
    private JTextField attendanceField;
    private JTextField lab1Field;
    private JTextField lab2Field;
    private JTextField lab3Field;
    private JLabel resultLabAvg;
    private JLabel resultClassStanding;
    private JLabel resultPass;
    private JLabel resultExcellent;
    private JLabel errorLabel;
    private JTextArea remarksArea;

    public PrelimGradeCalculator() {
        setTitle("Prelim Grade Calculator");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        // Use standard padding
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Header ---
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));

        JLabel mainTitle = new JLabel("Prelim Grade Calculator", SwingConstants.CENTER);
        JLabel subTitle = new JLabel("Target Grade Predictor", SwingConstants.CENTER);

        headerPanel.add(mainTitle);
        headerPanel.add(subTitle);
        add(headerPanel, BorderLayout.NORTH);

        // --- Input Panel ---
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));

        inputPanel.add(new JLabel("Attendance Score (0-100):"));
        attendanceField = new JTextField();
        inputPanel.add(attendanceField);

        inputPanel.add(new JLabel("Lab Work 1:"));
        lab1Field = new JTextField();
        inputPanel.add(lab1Field);

        inputPanel.add(new JLabel("Lab Work 2:"));
        lab2Field = new JTextField();
        inputPanel.add(lab2Field);

        inputPanel.add(new JLabel("Lab Work 3:"));
        lab3Field = new JTextField();
        inputPanel.add(lab3Field);

        add(inputPanel, BorderLayout.CENTER);

        // --- Bottom Operations ---
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

        JButton calculateBtn = new JButton("Calculate Required Scores");
        calculateBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        calculateBtn.addActionListener(new CalculateAction());
        bottomPanel.add(Box.createVerticalStrut(10));
        bottomPanel.add(calculateBtn);

        errorLabel = new JLabel("Please enter valid numeric values.");
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setVisible(false);
        bottomPanel.add(Box.createVerticalStrut(5));
        bottomPanel.add(errorLabel);

        bottomPanel.add(Box.createVerticalStrut(10));

        // --- Results ---
        JPanel resultsPanel = new JPanel(new GridLayout(4, 2, 5, 5));

        resultsPanel.add(new JLabel("Lab Average:"));
        resultLabAvg = new JLabel("0.00");
        resultsPanel.add(resultLabAvg);

        resultsPanel.add(new JLabel("Class Standing (30%):"));
        resultClassStanding = new JLabel("0.00");
        resultsPanel.add(resultClassStanding);

        resultsPanel.add(new JLabel("Required Exam for PASS (75):"));
        resultPass = new JLabel("--");
        resultsPanel.add(resultPass);

        resultsPanel.add(new JLabel("Required Exam for 100:"));
        resultExcellent = new JLabel("--");
        resultsPanel.add(resultExcellent);

        bottomPanel.add(resultsPanel);
        bottomPanel.add(Box.createVerticalStrut(10));

        // --- Output Summary ---
        remarksArea = new JTextArea(8, 30);
        remarksArea.setEditable(false);
        remarksArea.setLineWrap(true);
        remarksArea.setWrapStyleWord(true);
        // Keep Titled Border for section separation logic
        remarksArea.setBorder(BorderFactory.createTitledBorder("Full Report & Remarks"));

        bottomPanel.add(new JScrollPane(remarksArea));

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private class CalculateAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                errorLabel.setVisible(false);

                // 1. Parsing Inputs
                double attendance = Double.parseDouble(attendanceField.getText());
                double lab1 = Double.parseDouble(lab1Field.getText());
                double lab2 = Double.parseDouble(lab2Field.getText());
                double lab3 = Double.parseDouble(lab3Field.getText());

                // 2. Computations
                double labAverage = (lab1 + lab2 + lab3) / 3.0;
                double classStanding = (attendance * 0.40) + (labAverage * 0.60);

                double targetPass = 75.0;
                double targetExcellent = 100.0;

                // Formula: Exam = (Target - (CS * 0.30)) / 0.70
                double requiredPass = (targetPass - (classStanding * 0.30)) / 0.70;
                double requiredExcellent = (targetExcellent - (classStanding * 0.30)) / 0.70;

                // 3. Update UI Labels
                resultLabAvg.setText(String.format("%.2f", labAverage));
                resultClassStanding.setText(String.format("%.2f", classStanding));
                resultPass.setText(String.format("%.2f", requiredPass));
                resultExcellent.setText(String.format("%.2f", requiredExcellent));

                // 4. Generate Full Report
                StringBuilder report = new StringBuilder();
                report.append("--- Input Summary ---\n");
                report.append(String.format("Attendance Score: %.2f\n", attendance));
                report.append(String.format("Lab Grades: %.2f, %.2f, %.2f\n", lab1, lab2, lab3));
                report.append("\n--- Remarks ---\n");

                if (requiredPass <= 0) {
                    report.append("You have ALREADY PASSED based on Class Standing!\n");
                } else if (requiredPass > 100) {
                    report.append("Passing (75) is mathematically impossible this period.\n");
                } else {
                    report.append(String.format("You need %.2f on the exam to PASS.\n", requiredPass));
                }

                if (requiredExcellent <= 0) {
                    report.append("You are guaranteed an EXCELLENT grade!");
                } else if (requiredExcellent <= 100) {
                    report.append(
                            String.format("You need %.2f on the exam for EXCELLENT standing.", requiredExcellent));
                } else {
                    report.append("Excellent standing (100) is not reachable this period.");
                }

                remarksArea.setText(report.toString());

            } catch (NumberFormatException ex) {
                errorLabel.setVisible(true);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new PrelimGradeCalculator().setVisible(true);
        });
    }
}
