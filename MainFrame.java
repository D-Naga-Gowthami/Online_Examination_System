import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    private JButton createCourseButton;
    private JButton addQuestionButton;
    private JButton takeExamButton;
    private JButton viewResultsButton;

    public MainFrame() {
        setTitle("Online Examination System");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createCourseButton = new JButton("Create Course");
        addQuestionButton = new JButton("Add Question");
        takeExamButton = new JButton("Take Exam");
        viewResultsButton = new JButton("View Results");

        createCourseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new CreateCourseFrame();
            }
        });

        addQuestionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new AddQuestionFrame();
            }
        });

        takeExamButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new TakeExamFrame();
            }
        });

        viewResultsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ViewResultsFrame();
            }
        });

        JPanel panel = new JPanel();
        panel.add(createCourseButton);
        panel.add(addQuestionButton);
        panel.add(takeExamButton);
        panel.add(viewResultsButton);

        add(panel);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}
