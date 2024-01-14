package task;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class HelpDialog {

    public static void showHelpDialog(JFrame frame) {
        JTextArea textArea = new JTextArea(10, 30);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);

        int result = JOptionPane.showConfirmDialog(frame, scrollPane, "Help", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String userInput = textArea.getText();
            storeHelpToDatabase(userInput);
            showSuccessMessage();
        }
    }

    private static void storeHelpToDatabase(String helpText) {
        // Database connection parameters
        String url = "jdbc:mysql://localhost:3306/application";
        String username = "root";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            // SQL statement to insert data into the 'help' table
            String insertQuery = "INSERT INTO help (help) VALUES (?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, helpText);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error storing help information in the database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void showSuccessMessage() {
        JOptionPane.showMessageDialog(null, "Help information submitted successfully.");
        System.out.println("Success message displayed.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame mainFrame = new JFrame("Main Frame");
            mainFrame.setSize(500, 400);
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JButton helpButton = new JButton("Help");
            helpButton.addActionListener(e -> showHelpDialog(mainFrame));

            JPanel panel = new JPanel();
            panel.add(helpButton);

            mainFrame.add(panel);
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setVisible(true);
        });
    }
}
