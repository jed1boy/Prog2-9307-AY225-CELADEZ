package AttendanceTracker;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import javax.swing.*;

/**
 * Attendance tracker app. Uses Swing to collect name, course, current time, and a UUID for signature.
 */
public class AttendanceTracker {

    public static void main(String[] args) {
        // Swing needs EDT for thread safety.
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }

    private static void createAndShowGUI() {
        // Create the main window.
        JFrame frame = new JFrame("Attendance Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 350);
        frame.setLocationRelativeTo(null); // Center the window

        // Set up panel with GridBagLayout for layout.
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Add input fields.

        // Name field.
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Attendance Name:"), gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(20);
        panel.add(nameField, gbc);

        // Course/Year field.
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Course/Year:"), gbc);
        gbc.gridx = 1;
        JTextField courseField = new JTextField(20);
        panel.add(courseField, gbc);

        // Get current time.
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Time In:"), gbc);
        gbc.gridx = 1;
        String timeIn = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        );
        JTextField timeInField = new JTextField(timeIn);
        timeInField.setEditable(false); // System generated, so read-only
        panel.add(timeInField, gbc);

        // Generate UUID signature.
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("E-Signature:"), gbc);
        gbc.gridx = 1;
        String eSignature = UUID.randomUUID().toString();
        JTextField eSignatureField = new JTextField(eSignature);
        eSignatureField.setEditable(false); // System generated, so read-only
        panel.add(eSignatureField, gbc);

        // Add submit button.
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2; // Span across both columns
        JButton submitButton = new JButton("Submit Attendance");
        submitButton.addActionListener(e -> {
            String name = nameField.getText();
            String course = courseField.getText();

            // Validate inputs.
            if (name.trim().isEmpty() || course.trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Please fill in all fields.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
                );
            } else {
                // Show success message.
                String summary = String.format(
                    "Attendance Recorded Successfully!\n\nName: %s\nCourse/Year: %s\nTime In: %s\nE-Signature: %s",
                    name,
                    course,
                    timeInField.getText(),
                    eSignatureField.getText()
                );
                JOptionPane.showMessageDialog(
                    frame,
                    summary,
                    "Submission Successful",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
        panel.add(submitButton, gbc);

        // Add panel to frame.
        frame.add(panel);

        // Show the window.
        frame.setVisible(true);
    }
}
