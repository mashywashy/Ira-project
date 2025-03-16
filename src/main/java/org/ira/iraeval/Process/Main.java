package org.ira.iraeval.Process;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        int totalUnits = 0;
        StudentEval se = new StudentEval("bsit");
        Map<String, Boolean> subMap = new HashMap<>();

        subMap.put("sts101", true);        // Passed
        subMap.put("philo101", true);      // Passed
        subMap.put("cc-quameth22", true);  // Passed
        subMap.put("it-platech22", true);  // Passed
        subMap.put("cc-appsdev22", true);  // Passed
        subMap.put("cc-datastruc22", true); // Passed
        subMap.put("cc-datacom22", true);  // Passed
        subMap.put("pe104", true);         // Passed

        List<Subject> subs = se.getRecommendedSubjects(subMap, 2, 1);
        for(Subject sub : subs) {
            System.out.println(sub.getCode() + " " + sub.getUnits());
            totalUnits += sub.getUnits();
        }

        System.out.println("Total Units: " + totalUnits);
    }
}
