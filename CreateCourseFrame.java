import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CreateCourseFrame extends JFrame {
    private JTextField courseNameField;
    private JButton submitButton;

    public CreateCourseFrame() {
        setTitle("Create Course");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        courseNameField = new JTextField(20);
        submitButton = new JButton("Submit");

        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String courseName = courseNameField.getText();
                addCourseToDatabase(courseName);
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Course Name:"));
        panel.add(courseNameField);
        panel.add(submitButton);

        add(panel);
        setVisible(true);
    }

    private void addCourseToDatabase(String courseName) {
        String query = "INSERT INTO courses (course_name) VALUES (?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, courseName);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Course added successfully!");
        } catch (SQLException e) {
            ExceptionHandler.handleException(e);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CreateCourseFrame());
    }
}
