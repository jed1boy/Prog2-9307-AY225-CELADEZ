import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ClassRecordsApp extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtID, txtName, txtGrade, txtLab1, txtLab2, txtLab3, txtPrelim, txtAttendance;
    private JButton btnAdd, btnDelete;

    private String resolveCSVPath() {
        String[] paths = {"data/class_records.csv", "../data/class_records.csv", "class_records.csv"};
        for (String path : paths) {
            if (new File(path).exists()) {
                return path;
            }
        }
        return "data/class_records.csv"; // Default fallback
    }

    public ClassRecordsApp() {
        setTitle("Class Records Management");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Table setup
        String[] columnNames = {"StudentID", "first_name", "last_name", "LAB WORK 1", "LAB WORK 2", "LAB WORK 3", "PRELIM EXAM", "ATTENDANCE GRADE"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Input panel setup
        JPanel inputPanel = new JPanel(new GridLayout(5, 4, 5, 5));
        inputPanel.add(new JLabel("StudentID:"));
        txtID = new JTextField();
        inputPanel.add(txtID);

        inputPanel.add(new JLabel("First Name:"));
        txtName = new JTextField();
        inputPanel.add(txtName);

        inputPanel.add(new JLabel("Last Name:"));
        txtGrade = new JTextField();
        inputPanel.add(txtGrade);

        inputPanel.add(new JLabel("LAB WORK 1:"));
        txtLab1 = new JTextField();
        inputPanel.add(txtLab1);

        inputPanel.add(new JLabel("LAB WORK 2:"));
        txtLab2 = new JTextField();
        inputPanel.add(txtLab2);

        inputPanel.add(new JLabel("LAB WORK 3:"));
        txtLab3 = new JTextField();
        inputPanel.add(txtLab3);

        inputPanel.add(new JLabel("PRELIM EXAM:"));
        txtPrelim = new JTextField();
        inputPanel.add(txtPrelim);

        inputPanel.add(new JLabel("ATTENDANCE:"));
        txtAttendance = new JTextField();
        inputPanel.add(txtAttendance);

        btnAdd = new JButton("Add");
        inputPanel.add(btnAdd);

        btnDelete = new JButton("Delete");
        inputPanel.add(btnDelete);

        // Layout
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        // Load data from CSV
        loadCSVData(resolveCSVPath());

        // Action Listeners
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = txtID.getText();
                String firstName = txtName.getText();
                String lastName = txtGrade.getText();
                String lab1 = txtLab1.getText();
                String lab2 = txtLab2.getText();
                String lab3 = txtLab3.getText();
                String prelim = txtPrelim.getText();
                String attendance = txtAttendance.getText();

                if (!id.isEmpty() && !firstName.isEmpty() && !lastName.isEmpty() && 
                    !lab1.isEmpty() && !lab2.isEmpty() && !lab3.isEmpty() && 
                    !prelim.isEmpty() && !attendance.isEmpty()) {
                    tableModel.addRow(new Object[]{id, firstName, lastName, lab1, lab2, lab3, prelim, attendance});
                    saveCSVData(resolveCSVPath());
                    txtID.setText("");
                    txtName.setText("");
                    txtGrade.setText("");
                    txtLab1.setText("");
                    txtLab2.setText("");
                    txtLab3.setText("");
                    txtPrelim.setText("");
                    txtAttendance.setText("");
                } else {
                    JOptionPane.showMessageDialog(null, "Please fill all fields.");
                }
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    tableModel.removeRow(selectedRow);
                    saveCSVData(resolveCSVPath());
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a row to delete.");
                }
            }
        });
    }

    private void loadCSVData(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Skip header
                }
                String[] data = line.split(",");
                if (data.length == 8) {
                    tableModel.addRow(data);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading file: " + e.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveCSVData(String fileName) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            pw.println("StudentID,first_name,last_name,LAB WORK 1,LAB WORK 2,LAB WORK 3,PRELIM EXAM,ATTENDANCE GRADE"); // Header
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String studentID = tableModel.getValueAt(i, 0).toString();
                String firstName = tableModel.getValueAt(i, 1).toString();
                String lastName = tableModel.getValueAt(i, 2).toString();
                String lab1 = tableModel.getValueAt(i, 3).toString();
                String lab2 = tableModel.getValueAt(i, 4).toString();
                String lab3 = tableModel.getValueAt(i, 5).toString();
                String prelim = tableModel.getValueAt(i, 6).toString();
                String attendance = tableModel.getValueAt(i, 7).toString();
                pw.println(studentID + "," + firstName + "," + lastName + "," + lab1 + "," + lab2 + "," + lab3 + "," + prelim + "," + attendance);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ClassRecordsApp().setVisible(true);
        });
    }
}
