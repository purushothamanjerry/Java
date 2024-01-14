package task;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;  // Import Calendar
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddTask {
    private JTextField taskField;
    private JComboBox<String> monthComboBox;
    private JComboBox<String> yearComboBox;
    private JComboBox<String> dateComboBox;

    public void createAndShowGUI(JFrame parentFrame) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Add Task");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(400, 300);
            frame.setResizable(false); // Disable maximize option

            JPanel mainPanel = new JPanel(new BorderLayout());

            JPanel inputPanel = createInputPanel();
            mainPanel.add(inputPanel, BorderLayout.CENTER);

            JButton addButton = new JButton("Add Task");
            addButton.addActionListener(e -> addTask(frame));
            mainPanel.add(addButton, BorderLayout.SOUTH);

            frame.add(mainPanel);
            frame.setLocationRelativeTo(parentFrame); // Set the location relative to the parent frame
            frame.setVisible(true);
        });
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding

        // Set the background color
        inputPanel.setBackground(new Color(173, 216, 230)); // Light Blue color, you can choose any color

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Add Task label and text input
        JLabel taskLabel = new JLabel("Task:");
        inputPanel.add(taskLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Add this line to make the taskField expand horizontally
        taskField = new JTextField(30);
        inputPanel.add(taskField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        JLabel monthLabel = new JLabel("Month:");
        inputPanel.add(monthLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        monthComboBox = new JComboBox<>(months);
        monthComboBox.addActionListener(e -> updateDateComboBox());
        inputPanel.add(monthComboBox, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        JLabel yearLabel = new JLabel("Year:");
        inputPanel.add(yearLabel, gbc);

        gbc.gridx = 3;
        gbc.gridy = 1;
        yearComboBox = new JComboBox<>(); // Initialize yearComboBox
        inputPanel.add(yearComboBox, gbc);
        updateYearComboBox();
        yearComboBox.addActionListener(e -> updateDateComboBox());

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel dateLabel = new JLabel("Date:");
        inputPanel.add(dateLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        String[] dates = getDatesForMonth(months[0]);
        dateComboBox = new JComboBox<>(dates);
        inputPanel.add(dateComboBox, gbc);

        return inputPanel;
    }






    private void updateDateComboBox() {
        String selectedMonth = (String) monthComboBox.getSelectedItem();
        String[] dates = getDatesForMonth(selectedMonth);
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(dates);
        dateComboBox.setModel(model);
    }

    private String[] getDatesForMonth(String month) {
        int daysInMonth;
        switch (month) {
            case "February":
                daysInMonth = isLeapYear() ? 29 : 28;
                break;
            case "April":
            case "June":
            case "September":
            case "November":
                daysInMonth = 30;
                break;
            default:
                daysInMonth = 31;
        }

        String[] result = new String[daysInMonth];
        for (int i = 1; i <= daysInMonth; i++) {
            result[i - 1] = Integer.toString(i);
        }
        return result;
    }

    private void updateYearComboBox() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String[] years = new String[10];
        for (int i = 0; i < 10; i++) {
            years[i] = Integer.toString(currentYear + i);
        }
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(years);
        yearComboBox.setModel(model);
    }

    private boolean isLeapYear() {
        int selectedYear = Integer.parseInt((String) yearComboBox.getSelectedItem());
        return (selectedYear % 4 == 0 && selectedYear % 100 != 0) || (selectedYear % 400 == 0);
    }

    private void addTask(JFrame frame) {
        String taskName = taskField.getText();

        // Use the current date and time
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Month is 0-based in Calendar
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Get the day of the week
        String dayOfWeek = getDayOfWeek(year, month, day);

        // Database connection parameters for XAMPP
        String url = "jdbc:mysql://localhost:3306/application";
        String username = "root";
        String password = "";

        // Validate input fields
        if (taskName.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter a task name", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Establish a database connection
            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                // SQL statement to insert data into the 'tasks' table
                String insertQuery = "INSERT INTO tasks (task_name, month, year, date, day_of_week) VALUES (?, ?, ?, ?, ?)";

                try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                    preparedStatement.setString(1, taskName);
                    preparedStatement.setInt(2, month);
                    preparedStatement.setInt(3, year);
                    preparedStatement.setInt(4, day);
                    preparedStatement.setString(5, dayOfWeek);

                    // Execute the SQL statement
                    preparedStatement.executeUpdate();
                }
            }

            JOptionPane.showMessageDialog(frame, "Task added successfully");
            frame.dispose(); // Close the window after success message
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error storing task data in the database", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Print the exception details for debugging
        }
    }

    // Method to get the day of the week
    private String getDayOfWeek(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day); // Month is 0-based in Calendar
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        
        // Convert dayOfWeek to a string representation (e.g., "Sunday")
        String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        return daysOfWeek[dayOfWeek - 1];
    }
}