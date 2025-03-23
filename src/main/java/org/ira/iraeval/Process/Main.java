package org.ira.iraeval.Process;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        int totalUnits = 0;
        StudentEval se = new StudentEval("bsit");
        Map<String, Double> subMap = new HashMap<>();
        Random random = new Random();

        // Assign random grades between 1.0 and 5.0
        subMap.put("sts101", 1.5 + random.nextDouble() * 3.5);        // Random grade
        subMap.put("philo101", 1.5 + random.nextDouble() * 3.5);      // Random grade
        subMap.put("cc-quameth22", 1.5 + random.nextDouble() * 3.5);  // Random grade
        subMap.put("it-platech22", 1.5 + random.nextDouble() * 3.5);  // Random grade
        subMap.put("cc-appsdev22", 1.5 + random.nextDouble() * 3.5);  // Random grade
        subMap.put("cc-datastruc22", 1.5 + random.nextDouble() * 3.5); // Random grade
        subMap.put("cc-datacom22", 1.5 + random.nextDouble() * 3.5);  // Random grade
        subMap.put("pe104", 1.5 + random.nextDouble() * 3.5);         // Random grade

        // Get recommended subjects
        Map<Subject, String> recommendedSubjects = se.getRecommendedSubjects(subMap, 2, 1);

        // Display recommended subjects with indicators
        for (Map.Entry<Subject, String> entry : recommendedSubjects.entrySet()) {
            Subject subject = entry.getKey();
            String status = entry.getValue();
            System.out.println(subject.getCode() + " (" + subject.getUnits() + " units) - " + status);
            totalUnits += subject.getUnits();
        }

        System.out.println("Total Units: " + totalUnits);
    }
}
