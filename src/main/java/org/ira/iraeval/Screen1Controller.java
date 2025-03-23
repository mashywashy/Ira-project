package org.ira.iraeval;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import org.ira.iraeval.Process.StudentEval;
import org.ira.iraeval.Process.Subject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Screen1Controller {

    @FXML private TextField nameField;
    @FXML private TextField idNumberField;
    @FXML private ComboBox<String> newStudentComboBox;
    @FXML private ComboBox<String> programComboBox;
    @FXML private ComboBox<String> yearLevelComboBox;
    @FXML private ComboBox<String> semesterComboBox;
    @FXML private TextField subjectsField;
    @FXML private Button nextButton;
    @FXML private Button cancelButton;

    // Regex patterns for validation
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z.\\s]+$"); // Only letters, dots, and spaces
    private static final Pattern ID_PATTERN = Pattern.compile("^\\d{1,9}$"); // Only digits, max 9 digits

    @FXML
    public void initialize() {
        System.out.println("DEBUG: Initializing Screen1Controller");
        // Initialize ComboBoxes
        newStudentComboBox.getItems().addAll("Yes", "No");
        programComboBox.getItems().addAll("BSIT", "BSA", "BSN", "BSMT");
        yearLevelComboBox.getItems().addAll("1", "2", "3", "4");
        semesterComboBox.getItems().addAll("1", "2");

        // Add listener to newStudentComboBox to enable/disable subjectsField
        newStudentComboBox.setOnAction(event -> handleNewStudentSelection());

        // Handle Next button click
        nextButton.setOnAction(event -> handleNextButton());
        cancelButton.setOnAction(event -> handleCancelButton());
    }

    private void handleNewStudentSelection() {
        boolean isNewStudent = "Yes".equals(newStudentComboBox.getValue());
        subjectsField.setDisable(isNewStudent);
        yearLevelComboBox.setDisable(isNewStudent);
        semesterComboBox.setDisable(isNewStudent);
        if (isNewStudent) {
            subjectsField.clear();
            yearLevelComboBox.setValue(null);
            semesterComboBox.setValue(null);
        }
    }

    private void handleNextButton() {
        System.out.println("DEBUG: Next button clicked");
        String name = nameField.getText().trim();
        String id = idNumberField.getText().trim();
        String isNewStudent = newStudentComboBox.getValue();
        String program = programComboBox.getValue();
        String yearLevel = yearLevelComboBox.getValue();
        String semester = semesterComboBox.getValue();

        // Validate input fields
        if (name.isEmpty() || id.isEmpty() || isNewStudent == null || program == null) {
            showAlert("Error", "Please complete all required fields.");
            return;
        }

        // Validate name - only letters and dots allowed
        if (!isValidName(name)) {
            showAlert("Error", "Name should contain only letters, spaces, and dots (periods).");
            return;
        }

        // Validate ID - only digits, max 9 digits
        if (!isValidId(id)) {
            showAlert("Error", "ID Number should contain only digits and be 9 or fewer digits.");
            return;
        }

        System.out.println("DEBUG: Validation passed, processing as " + 
                          (isNewStudent.equals("Yes") ? "new student" : "continuing student"));

        if ("Yes".equals(isNewStudent)) {
            // For new students, directly show recommendations
            StudentEval se = new StudentEval(program);
            List<Subject> subjects = se.getRecommendedSubjects();
            
            // Convert list to map with all subjects marked as "Recommended"
            Map<Subject, String> recommendedSubjects = new HashMap<>();
            for (Subject subject : subjects) {
                recommendedSubjects.put(subject, "Recommended");
            }
            
            System.out.println("DEBUG: Displaying " + recommendedSubjects.size() + 
                              " recommended subjects for new student");
            displayRecommendedSubjects(name, id, program, recommendedSubjects);
        } else {
            // For continuing students, check the subjects count field
            if (subjectsField.getText().isEmpty() || !isNumeric(subjectsField.getText())) {
                showAlert("Error", "Please enter a valid number of subjects taken.");
                return;
            }

            if(Integer.parseInt(subjectsField.getText()) > 15) {
                showAlert("Error", "Please enter no more than 15");
                return;
            }

            int subjectCount = Integer.parseInt(subjectsField.getText());
            if (subjectCount <= 0) {
                showAlert("Error", "Number of subjects must be greater than zero.");
                return;
            }

            // Open the subject selection screen
            System.out.println("DEBUG: Opening subject selection with " + subjectCount + " subjects");
            openSubjectSelectionScreen(subjectCount, program, name, id, yearLevel, semester);
        }
    }

    /**
     * Validates that the name contains only letters, spaces, and dots
     */
    private boolean isValidName(String name) {
        return NAME_PATTERN.matcher(name).matches();
    }

    /**
     * Validates that the ID contains only digits and is 9 or fewer digits
     */
    private boolean isValidId(String id) {
        return ID_PATTERN.matcher(id).matches();
    }

    private void openSubjectSelectionScreen(int subjectCount, String program, String name, String id, String year, String semester) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("subject_input.fxml"));
            Parent root = loader.load();

            SubjectInputController controller = loader.getController();
            controller.setupSubjects(subjectCount, program, Integer.parseInt(year), Integer.parseInt(semester), name, id);

            Stage stage = new Stage();
            stage.setTitle("Subject Selection");
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();

            // Close the current window
            Stage currentStage = (Stage) nextButton.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not open subject selection screen: " + e.getMessage());
        }
    }

    private void displayRecommendedSubjects(String name, String id, String program, Map<Subject, String> recommendedSubjects) {
        try {
            System.out.println("DEBUG: Loading subject_output.fxml");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("subject_output.fxml"));
            Parent root = loader.load();

            SubjectOutputController controller = loader.getController();
            
            // For new students, we don't have input subjects
            System.out.println("DEBUG: Setting up controller with " + recommendedSubjects.size() + " subjects");
            controller.setupRecommendedSubjects(name, id, program, recommendedSubjects);

            for (Map.Entry<Subject, String> entry : recommendedSubjects.entrySet()) {
                Subject subject = entry.getKey();
                String status = entry.getValue();
                System.out.println("DEBUG: Subject: " + subject.getCode() + 
                                  " (" + subject.getUnits() + " units) - " + status);
            }

            Stage stage = new Stage();
            stage.setTitle("Recommended Subjects");
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();

            // Close the current window
            Stage currentStage = (Stage) nextButton.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not open recommended subjects screen: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        System.out.println("DEBUG: Showing alert - " + title + ": " + message);
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void handleCancelButton() {
        System.out.println("DEBUG: Cancel button clicked, closing window");
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}