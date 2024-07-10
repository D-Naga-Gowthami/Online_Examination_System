import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessageFrame extends JFrame {
    private JComboBox<String> userComboBox;
    private JTextArea messageArea;
    private JButton sendButton;

    public MessageFrame() {
        setTitle("Send Message");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        userComboBox = new JComboBox<>(getUsers().toArray(new String[0]));
        messageArea = new JTextArea(10, 30);
        sendButton = new JButton("Send");

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String recipient = (String) userComboBox.getSelectedItem();
                String message = messageArea.getText();
                sendMessage(recipient, message);
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Send to:"));
        panel.add(userComboBox);
        panel.add(new JScrollPane(messageArea));
        panel.add(sendButton);

        add(panel);
        setVisible(true);
    }

    private List<String> getUsers() {
        List<String> users = new ArrayList<>();
        String query = "SELECT username FROM users";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(rs.getString("username"));
            }
        } catch (SQLException e) {
            ExceptionHandler.handleException(e);
        }
        return users;
    }

    private void sendMessage(String recipient, String message) {
        String query = "INSERT INTO messages (sender_id, receiver_id, content) VALUES (?, ?, ?)";
        int senderId = getCurrentUserId(); // Implement this method to get the logged-in user's ID
        int receiverId = getUserIdByUsername(recipient); // Implement this method to get the user's ID by username
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            stmt.setString(3, message);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Message sent successfully!");
        } catch (SQLException e) {
            ExceptionHandler.handleException(e);
        }
    }

    private int getCurrentUserId() {
        // Implement this method to return the currently logged-in user's ID
        return 1; // Placeholder value
    }

    private int getUserIdByUsername(String username) {
        String query = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            ExceptionHandler.handleException(e);
        }
        return -1; // Return -1 if user not found
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MessageFrame());
    }
}
