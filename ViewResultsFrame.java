import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;  // Add this import statement
import java.util.List;      // Add this import statement

public class ViewResultsFrame extends JFrame {
    private JComboBox<String> courseComboBox;
    private JButton viewButton;
    private JTextArea resultsArea;

    public ViewResultsFrame() {
        setTitle("View Results");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        courseComboBox = new JComboBox<>(getCourses().toArray(new String[0]));
        viewButton = new JButton("View Results");
        resultsArea = new JTextArea(20, 40);
        resultsArea.setEditable(false);

        viewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedCourse = (String) courseComboBox.getSelectedItem();
                displayResults(selectedCourse);
            }
        });

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Course:"));
        topPanel.add(courseComboBox);
        topPanel.add(viewButton);

        add(topPanel, "North");
        add(new JScrollPane(resultsArea), "Center");

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

    private void displayResults(String courseName) {
        resultsArea.setText("");
        String query = "SELECT sa.student_id, q.question_text, sa.student_answer, q.correct_answer " +
                       "FROM student_answers sa " +
                       "JOIN questions q ON sa.question_id = q.id " +
                       "JOIN courses c ON q.course_id = c.id " +
                       "WHERE c.course_name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, courseName);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String studentId = rs.getString("student_id");
                    String questionText = rs.getString("question_text");
                    String studentAnswer = rs.getString("student_answer").trim();
                    String correctAnswer = rs.getString("correct_answer").trim();
                    boolean isCorrect = studentAnswer.equalsIgnoreCase(correctAnswer);
                    resultsArea.append("Student ID: " + studentId + "\n");
                    resultsArea.append("Question: " + questionText + "\n");
                    resultsArea.append("Your Answer: " + studentAnswer + "\n");
                    resultsArea.append("Correct Answer: " + correctAnswer + "\n");
                    resultsArea.append(isCorrect ? "Result: Correct\n\n" : "Result: Incorrect\n\n");
                }
            }
        } catch (SQLException e) {
            ExceptionHandler.handleException(e);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ViewResultsFrame());
    }
}
