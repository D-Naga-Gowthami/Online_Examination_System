import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddQuestionFrame extends JFrame {
    private JComboBox<String> courseComboBox;
    private JTextField questionTextField;
    private JTextField correctAnswerField;
    private JButton submitButton;

    public AddQuestionFrame() {
        setTitle("Add Question");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        courseComboBox = new JComboBox<>(getCourses().toArray(new String[0]));
        questionTextField = new JTextField(30);
        correctAnswerField = new JTextField(30);
        submitButton = new JButton("Submit");

        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedCourse = (String) courseComboBox.getSelectedItem();
                String questionText = questionTextField.getText();
                String correctAnswer = correctAnswerField.getText();
                addQuestionToDatabase(selectedCourse, questionText, correctAnswer);
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Course:"));
        panel.add(courseComboBox);
        panel.add(new JLabel("Question:"));
        panel.add(questionTextField);
        panel.add(new JLabel("Correct Answer:"));
        panel.add(correctAnswerField);
        panel.add(submitButton);

        add(panel);
        setVisible(true);
    }

    private List<String> getCourses() {
        List<String> courses = new ArrayList<>();
        String query = "SELECT course_name FROM courses";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                courses.add(rs.getString("course_name"));
            }
        } catch (SQLException e) {
            ExceptionHandler.handleException(e);
        }
        return courses;
    }

    private void addQuestionToDatabase(String courseName, String questionText, String correctAnswer) {
        String checkQuery = "SELECT COUNT(*) FROM questions WHERE course_id = (SELECT id FROM courses WHERE course_name = ?) AND question_text = ?";
        String insertQuery = "INSERT INTO questions (course_id, question_text, correct_answer) VALUES ((SELECT id FROM courses WHERE course_name = ?), ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {

            checkStmt.setString(1, courseName);
            checkStmt.setString(2, questionText);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this, "Question already exists in this course!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            insertStmt.setString(1, courseName);
            insertStmt.setString(2, questionText);
            insertStmt.setString(3, correctAnswer);
            insertStmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Question added successfully!");
        } catch (SQLException e) {
            ExceptionHandler.handleException(e);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AddQuestionFrame());
    }
}
