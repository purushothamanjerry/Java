package task;

import javax.swing.SwingUtilities;

public class TaskSchedulerApp {
	    public static void main(String[] args) {
	        SwingUtilities.invokeLater(() -> {
	            Home home = new Home();
	            home.createAndShowGUI();
	        });
	    }
	}



