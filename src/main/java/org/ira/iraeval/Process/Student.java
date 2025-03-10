package org.ira.iraeval.Process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Student {
    private String studentId;
    private String programCode;
    private boolean isNew;
    private int semester;
    private int year;
    private Map<String, Subject> takenSubjects;
    private List<String> currentSubjects;

    public Student(String studentId, String programCode, boolean isNew, int semester, int year) {
        this.studentId = studentId;
        this.programCode = programCode;
        this.semester = semester;
        this.year = year;
        this.isNew = isNew;
        this.takenSubjects = new HashMap<>();
        this.currentSubjects = new ArrayList<>();
    }

    public boolean isNew() {
        return isNew;
    }

    public String getProgramCode() {
        return programCode;
    }

    public void addTakenSubject(Subject subject) {
        takenSubjects.put(subject.getCode(), subject);
    }

    public void setCurrentSubjects(List<String> subjects) {
        this.currentSubjects = subjects;
    }

    public List<String> getCurrentSubjects() {
        return currentSubjects;
    }

    public Map<String, Subject> getTakenSubjects() {
        return takenSubjects;
    }
}