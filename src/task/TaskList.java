
package task;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class TaskList {
    private JFrame frame;
    private JList<String> taskList;
    private DefaultListModel<String> listModel;
    private int taskId; // Class-level variable for taskId

    public void createAndShowGUI() {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Task List");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(400, 300);
            frame.setResizable(false);

            JPanel mainPanel = new JPanel(new BorderLayout());

            listModel = new DefaultListModel<>();
            taskList = new JList<>(listModel);
            JScrollPane scrollPane = new JScrollPane(taskList);

            JButton completeButton = new JButton("Complete");
            completeButton.addActionListener(e -> markTaskAsComplete());
            JButton deleteButton = new JButton("Delete");
            deleteButton.addActionListener(e -> deleteSelectedTask());

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(completeButton);
            buttonPanel.add(deleteButton);

            mainPanel.add(scrollPane, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            frame.add(mainPanel);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Load tasks when the frame is displayed
            loadTasks();
        });
    }

    private void loadTasks() {
        // Database connection parameters for XAMPP
        String url = "jdbc:mysql://localhost:3306/application";
        String username = "root";
        String password = "";

        // Query to select all tasks
        String selectQuery = "SELECT id, task_name, month, year, date, status FROM tasks";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectQuery)) {

            // Clear the existing list
            listModel.clear();

            // Populate the list with tasks
         // Inside the loadTasks method
            while (resultSet.next()) {
                taskId = resultSet.getInt("id"); // Assign taskId at the class level
                String taskName = resultSet.getString("task_name");
                String taskDate = resultSet.getString("date") + " " + resultSet.getString("month") + " " + resultSet.getString("year");
                String taskStatus = resultSet.getString("status");

                // Set status to "Pending" if it is null
                if (taskStatus == null) {
                    taskStatus = "Pending";
                }

                String listItem = taskId + ". " + taskName + " - " + taskDate + " (" + taskStatus + ")";
                listModel.addElement(listItem);

                // Print the status for debugging
                System.out.println("Task ID: " + taskId + ", Status: " + taskStatus);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading tasks from the database", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void markTaskAsComplete() {
        // Get the selected task from the list
        int selectedIndex = taskList.getSelectedIndex();

        if (selectedIndex != -1) {
            // Extract the task ID from the selected list item
            String selectedItem = listModel.getElementAt(selectedIndex);
            taskId = Integer.parseInt(selectedItem.split("\\.")[0]);

            // Update the task status to "completed" in the database
            updateTaskStatus(taskId, "completed");

            // Reload the tasks to update the list
            loadTasks();
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a task to mark as complete", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedTask() {
        // Get the selected task from the list
        int selectedIndex = taskList.getSelectedIndex();

        if (selectedIndex != -1) {
            // Extract the task ID from the selected list item
            String selectedItem = listModel.getElementAt(selectedIndex);
            taskId = Integer.parseInt(selectedItem.split("\\.")[0]);

            // Delete the selected task from the database
            deleteTask(taskId);

            // Reload the tasks to update the list
            loadTasks();
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a task to delete", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTaskStatus(int taskId, String status) {
        // Database connection parameters for XAMPP
        String url = "jdbc:mysql://localhost:3306/application";
        String username = "root";
        String password = "";

        // Update query to mark task as complete
        String updateQuery = "UPDATE tasks SET status = ? WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            preparedStatement.setString(1, status);
            preparedStatement.setInt(2, taskId);

            // Execute the update query
            int rowsAffected = preparedStatement.executeUpdate();

            // Print the rows affected for debugging
            System.out.println("Rows affected: " + rowsAffected);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error updating task status in the database", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteTask(int taskId) {
        // Database connection parameters for XAMPP
        String url = "jdbc:mysql://localhost:3306/application";
        String username = "root";
        String password = "";

        // Delete query to remove task from the database
        String deleteQuery = "DELETE FROM tasks WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {

            preparedStatement.setInt(1, taskId);

            // Execute the delete query
            int rowsAffected = preparedStatement.executeUpdate();

            // Print the rows affected for debugging
            System.out.println("Rows affected: " + rowsAffected);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error deleting task from the database", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TaskList taskList = new TaskList();
            taskList.createAndShowGUI();
        });
    }
}