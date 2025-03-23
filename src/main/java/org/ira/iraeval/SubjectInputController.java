package org.ira.iraeval;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ira.iraeval.Process.StudentEval;
import org.ira.iraeval.Process.Subject;

public class SubjectInputController {

    @FXML
    private VBox subjectsContainer;

    @FXML
    private Button submitButton;

    @FXML
    private Button backButton;

    private List<SubjectEntry> subjectEntries = new ArrayList<>();
    private String program;
    private int year;
    private int semester;
    private String studentName;
    private String studentId;

    private class SubjectEntry {
        private ComboBox<String> subjectComboBox;
        private TextField gradeField;

        public SubjectEntry(ComboBox<String> subjectComboBox, TextField gradeField) {
            this.subjectComboBox = subjectComboBox;
            this.gradeField = gradeField;
        }

        public String getSubject() {
            return subjectComboBox.getValue();
        }

        public Double getGrade() {
            try {
                return Double.parseDouble(gradeField.getText());
            } catch (NumberFormatException e) {
                return null; // Invalid grade
            }
        }
    }

    @FXML
    public void initialize() {
        submitButton.setOnAction(this::handleSubmit);
        backButton.setOnAction(this::handleBack);
    }

    public void setupSubjects(int count, String program, int year, int semester, String studentName, String studentId) {
        this.program = program;
        this.year = year;
        this.semester = semester;
        this.studentName = studentName;
        this.studentId = studentId;

        // Get all subjects for the program to populate the ComboBoxes
        StudentEval studentEval = new StudentEval(program);
        List<String> subjectCodes = new ArrayList<>();
        studentEval.getAllSubjects().forEach(subject -> subjectCodes.add(subject.getCode()));
        
        // Sort the subject codes
        Collections.sort(subjectCodes);
        System.out.println("DEBUG: Sorted subject codes: " + subjectCodes);

        // Create the specified number of subject entries
        for (int i = 0; i < count; i++) {
            createSubjectEntry(i + 1, subjectCodes);
        }
    }

    private void createSubjectEntry(int index, List<String> subjectCodes) {
        HBox entryContainer = new HBox(10);
        entryContainer.setPadding(new Insets(5, 10, 5, 10));

        Label indexLabel = new Label("Subject " + index + ":");
        indexLabel.setPrefWidth(80);

        ComboBox<String> subjectComboBox = new ComboBox<>();
        // Add sorted items to the combo box
        ObservableList<String> sortedItems = FXCollections.observableArrayList(subjectCodes);
        sortedItems.sort(String::compareTo);
        subjectComboBox.setItems(sortedItems);
        
        subjectComboBox.setPrefWidth(150);
        subjectComboBox.setPromptText("Select Subject");

        // Add listener for subject selection
        subjectComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (oldValue != null) {
                // Add the old value back to other combo boxes (maintaining sort)
                for (SubjectEntry entry : subjectEntries) {
                    if (entry.subjectComboBox != subjectComboBox) {
                        addItemSorted(entry.subjectComboBox, oldValue);
                    }
                }
            }
            if (newValue != null) {
                // Remove the new value from other combo boxes
                for (SubjectEntry entry : subjectEntries) {
                    if (entry.subjectComboBox != subjectComboBox) {
                        entry.subjectComboBox.getItems().remove(newValue);
                    }
                }
            }
        });

        Label gradeLabel = new Label("Grade:");
        gradeLabel.setPrefWidth(50);

        TextField gradeField = new TextField();
        gradeField.setPrefWidth(100);
        gradeField.setPromptText("Enter Grade (1.0 - 5.0)");

        entryContainer.getChildren().addAll(indexLabel, subjectComboBox, gradeLabel, gradeField);
        subjectsContainer.getChildren().add(entryContainer);

        subjectEntries.add(new SubjectEntry(subjectComboBox, gradeField));
    }
    
    /**
     * Adds an item to a ComboBox while maintaining alphabetical sort order
     * @param comboBox The ComboBox to add the item to
     * @param item The item to add
     */
    private void addItemSorted(ComboBox<String> comboBox, String item) {
        if (item == null || comboBox == null) return;
        
        // Get current items
        ObservableList<String> items = comboBox.getItems();
        
        // Add the new item
        items.add(item);
        
        // Sort the items
        FXCollections.sort(items);
        
        System.out.println("DEBUG: Added item " + item + " to combo box, sorted items: " + items);
    }

    private void handleSubmit(ActionEvent event) {
        Map<String, Double> subjectGradeMap = new HashMap<>();
        boolean isValid = true;

        for (SubjectEntry entry : subjectEntries) {
            String subject = entry.getSubject();
            Double grade = entry.getGrade();

            if (subject == null || grade == null || grade < 1.0 || grade > 5.0) {
                isValid = false;
                break;
            }

            subjectGradeMap.put(subject, grade);
        }

        if (!isValid) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please complete all subject entries with valid grades (1.0 - 5.0).");
            return;
        }

        // Get recommended subjects
        StudentEval studentEval = new StudentEval(program);
        Map<Subject, String> recommendedSubjects = studentEval.getRecommendedSubjects(subjectGradeMap, year, semester);

        // Pass data to RecommendedSubjectsController
        openRecommendedSubjectsWindow(recommendedSubjects);
    }

    private void handleBack(ActionEvent event) {
        try {
            // Load the main screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("screen1.fxml"));
            Parent root = loader.load();

            javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();

            // Create new scene with the main screen
            Scene scene = new Scene(root);

            // Get the current stage and set the new scene
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not return to main screen.");
        }
    }

    private void openRecommendedSubjectsWindow(Map<Subject, String> recommendedSubjects) {
        System.out.println("DEBUG: Opening recommended subjects window");
        System.out.println("DEBUG: Student: " + studentName + " (" + studentId + "), Program: " + program);
        System.out.println("DEBUG: Recommended subjects count: " + recommendedSubjects.size());
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("subject_output.fxml"));
            Parent root = loader.load();
            System.out.println("DEBUG: Loaded subject_output.fxml");

            SubjectOutputController controller = loader.getController();
            
            // Also pass the input subjects
            Map<String, Double> subjectGradeMap = new HashMap<>();
            for (SubjectEntry entry : subjectEntries) {
                if (entry.getSubject() != null && entry.getGrade() != null) {
                    subjectGradeMap.put(entry.getSubject(), entry.getGrade());
                    System.out.println("DEBUG: Added input subject: " + entry.getSubject() + 
                                      " - Grade: " + entry.getGrade());
                }
            }
            
            System.out.println("DEBUG: Passing " + subjectGradeMap.size() + " input subjects");
            controller.setupRecommendedSubjects(studentName, studentId, program, subjectGradeMap, recommendedSubjects);

            Stage stage = new Stage();
            stage.setTitle("Recommended Subjects");
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            System.out.println("DEBUG: Showing recommended subjects window");
            stage.show();

            // Close the current window
            System.out.println("DEBUG: Closing subject input window");
            ((Stage) submitButton.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("DEBUG: ERROR opening recommended subjects window: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open recommended subjects window: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
