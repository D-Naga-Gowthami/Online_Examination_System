import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CreateExamFrame extends JFrame {
    private JTextField courseNameField;
    private JTextField durationField;
    private JButton submitButton;

    public CreateExamFrame() {
        setTitle("Create Exam");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        courseNameField = new JTextField(20);
        durationField = new JTextField(5);
        submitButton = new JButton("Submit");

        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String courseName = courseNameField.getText();
                int duration;
                try {
                    duration = Integer.parseInt(durationField.getText());
                    addExamToDatabase(courseName, duration);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Duration must be a valid integer", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Course Name:"));
        panel.add(courseNameField);
        panel.add(new JLabel("Duration:"));
        panel.add(durationField);
        panel.add(submitButton);

        add(panel);
        setVisible(true);
    }

    private void addExamToDatabase(String courseName, int duration) {
        String checkQuery = "SELECT COUNT(*) FROM exams WHERE course_name = ?";
        String insertQuery = "INSERT INTO exams (course_name, duration) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {

            checkStmt.setString(1, courseName);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this, "Exam with this course name already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            insertStmt.setString(1, courseName);
            insertStmt.setInt(2, duration);
            insertStmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Exam added successfully!");
        } catch (SQLException e) {
            ExceptionHandler.handleException(e);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new CreateExamFrame();
            }
        });
    }
}
