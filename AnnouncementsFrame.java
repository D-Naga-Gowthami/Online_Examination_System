import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AnnouncementsFrame extends JFrame {
    private JTextArea announcementArea;
    private JTextField announcementField;
    private JButton postButton;

    public AnnouncementsFrame() {
        setTitle("Announcements");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        announcementField = new JTextField(30);
        postButton = new JButton("Post");

        postButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String announcement = announcementField.getText();
                postAnnouncement(announcement);
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Announcement:"));
        panel.add(announcementField);
        panel.add(postButton);

        announcementArea = new JTextArea(20, 40);
        announcementArea.setEditable(false);

        add(panel, "North");
        add(new JScrollPane(announcementArea), "Center");

        loadAnnouncements();

        setVisible(true);
    }

    private void loadAnnouncements() {
        String query = "SELECT announcement FROM announcements ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String announcement = rs.getString("announcement");
                announcementArea.append(announcement + "\n\n");
            }
        } catch (SQLException e) {
            ExceptionHandler.handleException(e);
        }
    }

    private void postAnnouncement(String announcement) {
        String query = "INSERT INTO announcements (announcement) VALUES (?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, announcement);
            stmt.executeUpdate();
            announcementArea.append(announcement + "\n\n");
        } catch (SQLException e) {
            ExceptionHandler.handleException(e);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AnnouncementsFrame());
    }
}
