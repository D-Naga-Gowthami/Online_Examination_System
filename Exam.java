import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Exam {
    private String courseName;
    private int duration;

    public Exam(String courseName, int duration) {
        this.courseName = courseName;
        this.duration = duration;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void saveToDatabase() {
        String sql = "INSERT INTO exams (course_name, duration) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, courseName);
            pstmt.setInt(2, duration);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            ExceptionHandler.handleException(e);
        }
    }
}
