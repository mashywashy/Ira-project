package org.ira.iraeval;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.ira.iraeval.Process.Subject;

import java.util.HashMap;
import java.util.Map;

public class SubjectOutputController {

    @FXML
    private Label nameLabel;

    @FXML
    private Label idLabel;

    @FXML
    private Label programLabel;

    @FXML
    private VBox mainContainer;

    @FXML
    private Button printButton;

    @FXML
    private Button closeButton;

    @FXML
    private Label totalUnitsLabel;

    private boolean isNewStudent = true;
    private Map<String, Double> inputSubjects = new HashMap<>();

    @FXML
    public void initialize() {
        System.out.println("DEBUG: SubjectOutputController initialized");
        // Set up buttons if they exist
        if (printButton != null) {
            printButton.setOnAction(event -> handlePrint());
        } else {
            System.out.println("DEBUG: printButton is null");
        }
        
        if (closeButton != null) {
            closeButton.setOnAction(event -> handleClose());
        } else {
            System.out.println("DEBUG: closeButton is null");
        }
    }

    /**
     * Sets up the controller with student info and recommended subjects
     * This overload is used for continuing students and includes the input subjects
     */
    public void setupRecommendedSubjects(String name, String id, String program, 
                                         Map<String, Double> inputSubjects,
                                         Map<Subject, String> recommendedSubjects) {
        System.out.println("DEBUG: Setting up with input subjects (continuing student)");
        System.out.println("DEBUG: Student: " + name + " (" + id + "), Program: " + program);
        System.out.println("DEBUG: Input subjects count: " + (inputSubjects != null ? inputSubjects.size() : "null"));
        
        isNewStudent = false;
        this.inputSubjects = inputSubjects;
        
        // Print input subjects for debugging
        if (inputSubjects != null) {
            System.out.println("DEBUG: Input subjects with grades:");
            for (Map.Entry<String, Double> entry : inputSubjects.entrySet()) {
                System.out.println("DEBUG: " + entry.getKey() + " - Grade: " + entry.getValue());
            }
        }
        
        setupRecommendedSubjects(name, id, program, recommendedSubjects);
    }

    /**
     * Sets up the controller with student info and recommended subjects
     */
    public void setupRecommendedSubjects(String name, String id, String program, 
                                         Map<Subject, String> recommendedSubjects) {
        System.out.println("DEBUG: Setting up recommended subjects");
        System.out.println("DEBUG: Student: " + name + " (" + id + "), Program: " + program);
        System.out.println("DEBUG: isNewStudent: " + isNewStudent);
        System.out.println("DEBUG: Recommended subjects count: " + 
            (recommendedSubjects != null ? recommendedSubjects.size() : "null"));
        
        // Set student information if labels exist
        if (nameLabel != null) {
            nameLabel.setText(name);
        } else {
            System.out.println("DEBUG: nameLabel is null");
        }
        
        if (idLabel != null) {
            idLabel.setText(id);
        } else {
            System.out.println("DEBUG: idLabel is null");
        }
        
        if (programLabel != null) {
            programLabel.setText(program);
        } else {
            System.out.println("DEBUG: programLabel is null");
        }
        
        // Calculate and set the total units
        int totalUnits = recommendedSubjects.keySet().stream()
                .mapToInt(Subject::getUnits)
                .sum();
                
        if (totalUnitsLabel != null) {
            totalUnitsLabel.setText(String.valueOf(totalUnits));
            System.out.println("DEBUG: Set totalUnitsLabel to " + totalUnits);
        } else {
            System.out.println("DEBUG: totalUnitsLabel is null");
        }

        if (mainContainer != null) {
            System.out.println("DEBUG: Building UI in mainContainer");
            mainContainer.getChildren().clear();
            mainContainer.setSpacing(10);
            mainContainer.setPadding(new Insets(20));

            // Create a horizontal layout for the three columns
            HBox columnsContainer = new HBox(20);
            columnsContainer.setAlignment(Pos.TOP_CENTER);
            
            // For continuing students, show three columns
            if (!isNewStudent) {
                System.out.println("DEBUG: Creating three columns for continuing student");
                
                // Column 1: Input Subjects
                VBox inputColumn = createInputSubjectsColumn();
                
                // Column 2: Retake Subjects
                VBox retakeColumn = createRetakeSubjectsColumn(recommendedSubjects);
                
                // Column 3: Recommended Subjects
                VBox recommendedColumn = createRecommendedSubjectsColumn(recommendedSubjects);
                
                columnsContainer.getChildren().addAll(inputColumn, retakeColumn, recommendedColumn);
            } 
            // For new students, just show one column with all recommended subjects
            else {
                System.out.println("DEBUG: Creating one column for new student");
                System.out.println("DEBUG: Recommended subjects details:");
                for (Map.Entry<Subject, String> entry : recommendedSubjects.entrySet()) {
                    Subject subject = entry.getKey();
                    System.out.println("DEBUG: " + subject.getCode() + " (" + subject.getUnits() + " units) - " + entry.getValue());
                }
                VBox allSubjectsColumn = createAllSubjectsColumn(recommendedSubjects);
                columnsContainer.getChildren().add(allSubjectsColumn);
            }
            
            // Don't need to add totalUnitsRow since we're updating the FXML label directly
            // mainContainer.getChildren().addAll(columnsContainer, totalUnitsRow);
            mainContainer.getChildren().add(columnsContainer);
            
            System.out.println("DEBUG: UI creation complete");
        } else {
            System.out.println("DEBUG: mainContainer is null");
        }
    }

    private VBox createInputSubjectsColumn() {
        System.out.println("DEBUG: Creating input subjects column");
        VBox column = new VBox(10);
        column.setPrefWidth(300);
        column.setStyle("-fx-border-color: #CCCCCC; -fx-border-width: 1; -fx-padding: 10;");
        
        Label titleLabel = new Label("Input Subjects with Grades");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        column.getChildren().add(titleLabel);
        
        // Add each input subject and grade
        System.out.println("DEBUG: Adding " + inputSubjects.size() + " input subjects to column");
        for (Map.Entry<String, Double> entry : inputSubjects.entrySet()) {
            HBox row = new HBox(10);
            Label codeLabel = new Label(entry.getKey());
            codeLabel.setPrefWidth(150);
            
            Label gradeLabel = new Label(String.format("%.1f", entry.getValue()));
            gradeLabel.setPrefWidth(50);
            
            row.getChildren().addAll(codeLabel, gradeLabel);
            column.getChildren().add(row);
            System.out.println("DEBUG: Added input subject: " + entry.getKey() + " - Grade: " + entry.getValue());
        }
        
        return column;
    }
    
    private VBox createRetakeSubjectsColumn(Map<Subject, String> recommendedSubjects) {
        System.out.println("DEBUG: Creating retake subjects column");
        VBox column = new VBox(10);
        column.setPrefWidth(300);
        column.setStyle("-fx-border-color: #FFCCCC; -fx-background-color: #FFEEEE; -fx-border-width: 1; -fx-padding: 10;");
        
        Label titleLabel = new Label("Subjects to Retake");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        column.getChildren().add(titleLabel);
        
        // Add each subject that needs retaking
        boolean hasRetakes = false;
        int retakeCount = 0;
        
        for (Map.Entry<Subject, String> entry : recommendedSubjects.entrySet()) {
            if ("Retake".equals(entry.getValue())) {
                hasRetakes = true;
                retakeCount++;
                Subject subject = entry.getKey();
                
                HBox row = new HBox(10);
                Label codeLabel = new Label(subject.getCode());
                codeLabel.setPrefWidth(150);
                
                Label unitsLabel = new Label(subject.getUnits() + " units");
                unitsLabel.setPrefWidth(80);
                
                row.getChildren().addAll(codeLabel, unitsLabel);
                column.getChildren().add(row);
                System.out.println("DEBUG: Added retake subject: " + subject.getCode() + " (" + subject.getUnits() + " units)");
            }
        }
        
        System.out.println("DEBUG: Found " + retakeCount + " subjects to retake");
        
        if (!hasRetakes) {
            Label noRetakesLabel = new Label("No subjects to retake.");
            column.getChildren().add(noRetakesLabel);
            System.out.println("DEBUG: No retake subjects found");
        }
        
        return column;
    }
    
    private VBox createRecommendedSubjectsColumn(Map<Subject, String> recommendedSubjects) {
        System.out.println("DEBUG: Creating recommended subjects column");
        VBox column = new VBox(10);
        column.setPrefWidth(300);
        column.setStyle("-fx-border-color: #CCFFCC; -fx-background-color: #EEFFEE; -fx-border-width: 1; -fx-padding: 10;");
        
        Label titleLabel = new Label("Recommended New Subjects");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        column.getChildren().add(titleLabel);
        
        // Add each recommended subject
        boolean hasRecommended = false;
        int recommendedCount = 0;
        
        for (Map.Entry<Subject, String> entry : recommendedSubjects.entrySet()) {
            if ("Recommended".equals(entry.getValue())) {
                hasRecommended = true;
                recommendedCount++;
                Subject subject = entry.getKey();
                
                HBox row = new HBox(10);
                Label codeLabel = new Label(subject.getCode());
                codeLabel.setPrefWidth(150);
                
                Label unitsLabel = new Label(subject.getUnits() + " units");
                unitsLabel.setPrefWidth(80);
                
                row.getChildren().addAll(codeLabel, unitsLabel);
                column.getChildren().add(row);
                System.out.println("DEBUG: Added recommended subject: " + subject.getCode() + " (" + subject.getUnits() + " units)");
            }
        }
        
        System.out.println("DEBUG: Found " + recommendedCount + " recommended subjects");
        
        if (!hasRecommended) {
            Label noRecommendedLabel = new Label("No new subjects recommended.");
            column.getChildren().add(noRecommendedLabel);
            System.out.println("DEBUG: No recommended subjects found");
        }
        
        return column;
    }
    
    private VBox createAllSubjectsColumn(Map<Subject, String> recommendedSubjects) {
        System.out.println("DEBUG: Creating all subjects column for new student");
        VBox column = new VBox(10);
        column.setPrefWidth(400);
        column.setStyle("-fx-border-color: #CCCCFF; -fx-background-color: #EEEEFF; -fx-border-width: 1; -fx-padding: 10;");
        
        Label titleLabel = new Label("All Recommended Subjects");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        column.getChildren().add(titleLabel);
        
        // Add all subjects for new students
        System.out.println("DEBUG: Adding " + recommendedSubjects.size() + " subjects to column");
        for (Map.Entry<Subject, String> entry : recommendedSubjects.entrySet()) {
            Subject subject = entry.getKey();
            
            HBox row = new HBox(10);
            Label codeLabel = new Label(subject.getCode());
            codeLabel.setPrefWidth(150);
            
            Label unitsLabel = new Label(subject.getUnits() + " units");
            unitsLabel.setPrefWidth(80);
            
            row.getChildren().addAll(codeLabel, unitsLabel);
            column.getChildren().add(row);
            System.out.println("DEBUG: Added subject: " + subject.getCode() + " (" + subject.getUnits() + " units)");
        }
        
        return column;
    }
    
    private HBox createTotalUnitsRow(Map<Subject, String> recommendedSubjects) {
        System.out.println("DEBUG: Creating total units row");
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_RIGHT);
        row.setPadding(new Insets(20, 10, 10, 10));
        
        // Calculate total units
        int totalUnits = recommendedSubjects.keySet().stream()
                .mapToInt(Subject::getUnits)
                .sum();
        
        System.out.println("DEBUG: Total units: " + totalUnits);
        
        Label totalLabel = new Label("Total Units: ");
        totalLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        Label unitsLabel = new Label(String.valueOf(totalUnits));
        unitsLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        row.getChildren().addAll(totalLabel, unitsLabel);
        return row;
    }

    @FXML
    private void handlePrint() {
        System.out.println("DEBUG: Print button clicked");
        if (mainContainer == null) {
            System.out.println("DEBUG: Cannot print - mainContainer is null");
            return;
        }
        
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(mainContainer.getScene().getWindow())) {
            System.out.println("DEBUG: Printing...");
            boolean success = job.printPage(mainContainer.getScene().getRoot());
            if (success) {
                job.endJob();
                System.out.println("DEBUG: Print job completed successfully");
            } else {
                System.out.println("DEBUG: Print job failed");
            }
        } else {
            System.out.println("DEBUG: Print dialog was cancelled or could not be created");
        }
    }

    @FXML
    private void handleClose() {
        System.out.println("DEBUG: Close button clicked");
        if (closeButton == null) {
            System.out.println("DEBUG: Cannot close - closeButton is null");
            return;
        }
        
        Stage stage = (Stage) closeButton.getScene().getWindow();
        System.out.println("DEBUG: Closing window");
        stage.close();
    }
}

