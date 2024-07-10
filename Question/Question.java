import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Question {
    private String questionText;
    private String correctAnswer;

    public Question(String questionText, String correctAnswer) {
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public void saveToDatabase() {
        String sql = "INSERT INTO questions (question_text, correct_answer) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, questionText);
            pstmt.setString(2, correctAnswer);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            ExceptionHandler.handleException(e);
        }
    }
}
