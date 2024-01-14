package task;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Home {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }

    static void createAndShowGUI() {
        JFrame frame = new JFrame("Task Scheduler");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);
        frame.getContentPane().setBackground(new Color(0x5ea3bd));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel boxesPanel = createBoxesPanel(frame);
        mainPanel.add(boxesPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(0x5ea3bd));
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setResizable(false);

        frame.setVisible(true);
    }

    private static JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(0x1f2b4d));
        headerPanel.setLayout(new GridBagLayout());

        JLabel headerLabel = new JLabel("Task Scheduler");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        headerPanel.add(headerLabel, gbc);

        return headerPanel;
    }

    private static JPanel createBoxesPanel(JFrame frame) {
        JPanel boxesPanel = new JPanel();
        boxesPanel.setBackground(new Color(0x5ea3bd));
        boxesPanel.setLayout(new BoxLayout(boxesPanel, BoxLayout.Y_AXIS));

        String[] boxLabels = {"Add", "List", "Notification", "Help", "Exit"};
        for (String label : boxLabels) {
            JButton boxButton = createBoxButton(label, frame);
            boxesPanel.add(Box.createVerticalStrut(15));
            boxesPanel.add(boxButton);
        }

        return boxesPanel;
    }

    private static JButton createBoxButton(String buttonText, JFrame frame) {
        JButton boxButton = new JButton(buttonText);
        boxButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boxButton.setForeground(new Color(0x1f2b4d));
        boxButton.setBackground(new Color(0x64b6ac));
        boxButton.setFont(new Font("Arial", Font.PLAIN, 18));
        boxButton.setPreferredSize(new Dimension(200, 50));

        boxButton.addActionListener(e -> {
            if (buttonText.equals("Add")) {
                openAddTaskWindow(frame);
            } else if (buttonText.equals("List")) {
                openTaskListWindow();
            } else if (buttonText.equals("Exit")) {
                System.exit(0);
            } else if (buttonText.equals("Help")) {
                // Call HelpDialog directly
                HelpDialog.showHelpDialog(frame);
            } else {
                System.out.println(buttonText + " button clicked!");
            }
        });

        return boxButton;
    }

    private static void openTaskListWindow() {
        TaskList taskList = new TaskList();
        taskList.createAndShowGUI();
    }

    private static void openAddTaskWindow(JFrame frame) {
        AddTask addTask = new AddTask();
        addTask.createAndShowGUI(frame);
    }
}
