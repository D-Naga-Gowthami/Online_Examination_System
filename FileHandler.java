import java.io.*;
import java.util.ArrayList;

public class FileHandler {
    public void writeQuestionsToFile(ArrayList<Question> questions, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Question question : questions) {
                writer.write(question.getQuestionText() + "," + question.getCorrectAnswer());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
        }
    }

    public ArrayList<Question> readQuestionsFromFile(String filename) {
        ArrayList<Question> questions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                questions.add(new Question(parts[0], parts[1]));
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file.");
        }
        return questions;
    }
}
