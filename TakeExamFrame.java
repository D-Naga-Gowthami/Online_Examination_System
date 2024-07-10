import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TakeExamFrame extends JFrame {
    private JComboBox<String> courseComboBox;
    private JButton startButton;
    private JPanel questionPanel;
    private JButton submitButton;
    private List<QuestionPanel> questionPanels;
    private String studentId = "student123"; // Replace with actual student ID logic

    public TakeExamFrame() {
        setTitle("Take Exam");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        courseComboBox = new JComboBox<>(getCourses().toArray(new String[0]));
        startButton = new JButton("Start Exam");
        questionPanel = new JPanel();
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
        submitButton = new JButton("Submit Exam");
        submitButton.setEnabled(false);

        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedCourse = (String) courseComboBox.getSelectedItem();
                loadQuestions(selectedCourse);
            }
        });

        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                submitExam();
            }
        });

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Course:"));
        topPanel.add(courseComboBox);
        topPanel.add(startButton);

        add(topPanel, "North");
        add(new JScrollPane(questionPanel), "Center");
        add(submitButton, "South");

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

    private void loadQuestions(String courseName) {
        questionPanel.removeAll();
        questionPanels = new ArrayList<>();

        String query = "SELECT q.id, q.question_text FROM questions q JOIN courses c ON q.course_id = c.id WHERE c.course_name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, courseName);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int questionId = rs.getInt("id");
                    String questionText = rs.getString("question_text");
                    QuestionPanel qp = new QuestionPanel(questionId, questionText);
                    questionPanels.add(qp);
                    questionPanel.add(qp);
                }
            }
        } catch (SQLException e) {
            ExceptionHandler.handleException(e);
        }
        questionPanel.revalidate();
        questionPanel.repaint();
        submitButton.setEnabled(true);
    }

    private void submitExam() {
        String insertQuery = "INSERT INTO student_answers (student_id, question_id, student_answer) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            for (QuestionPanel qp : questionPanels) {
                stmt.setString(1, studentId);
                stmt.setInt(2, qp.getQuestionId());
                stmt.setString(3, qp.getAnswer());
                stmt.addBatch();
            }
            stmt.executeBatch();
            JOptionPane.showMessageDialog(this, "Exam submitted successfully!");
        } catch (SQLException e) {
            ExceptionHandler.handleException(e);
        }
    }

    private class QuestionPanel extends JPanel {
        private int questionId;
        private JLabel questionLabel;
        private JTextField answerField;

        public QuestionPanel(int questionId, String questionText) {
            this.questionId = questionId;
            questionLabel = new JLabel(questionText);
            answerField = new JTextField(20);
            add(questionLabel);
            add(answerField);
        }

        public int getQuestionId() {
            return questionId;
        }

        public String getAnswer() {
            return answerField.getText();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TakeExamFrame());
    }
}
