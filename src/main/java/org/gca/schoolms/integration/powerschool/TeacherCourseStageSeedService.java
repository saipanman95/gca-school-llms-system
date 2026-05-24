package org.gca.schoolms.integration.powerschool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class TeacherCourseStageSeedService {

    public List<TeacherCourseStage> loadSeedRows() throws IOException {
        ClassPathResource resource = new ClassPathResource("seed/teacher_course_clean.csv");
        List<TeacherCourseStage> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String header = reader.readLine();
            if (header == null) {
                return rows;
            }
            char delimiter = detectDelimiter(header);
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] columns = parseDelimitedRow(line, delimiter);
                if (columns.length < 7) {
                    continue;
                }
                String teacherName = blankToNull(columns[0]);
                String[] teacherParts = splitTeacherName(teacherName);
                String expression = blankToNull(columns[1]);
                String schoolYear = blankToNull(columns[2]);
                String courseNumber = blankToNull(columns[3]);
                String courseName = blankToNull(columns[4]);
                String sectionNumber = blankToNull(columns[5]);
                String room = blankToNull(columns[6]);
                rows.add(new TeacherCourseStage(
                    teacherName,
                    teacherParts[0],
                    teacherParts[1],
                    expression,
                    schoolYear,
                    courseNumber,
                    courseName,
                    sectionNumber,
                    room,
                    deriveGradeLevel(courseNumber, courseName)
                ));
            }
        }
        return rows;
    }

    private char detectDelimiter(String header) {
        return header.indexOf('\t') >= 0 ? '\t' : ',';
    }

    private String[] parseDelimitedRow(String line, char delimiter) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
                continue;
            }
            if (ch == delimiter && !inQuotes) {
                values.add(current.toString());
                current.setLength(0);
                continue;
            }
            current.append(ch);
        }
        values.add(current.toString());
        return values.toArray(String[]::new);
    }

    private String blankToNull(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private String[] splitTeacherName(String teacherName) {
        if (teacherName == null) {
            return new String[]{null, null};
        }
        String[] parts = teacherName.split(",", 2);
        String lastName = parts.length > 0 ? blankToNull(parts[0]) : null;
        String firstName = parts.length > 1 ? blankToNull(parts[1]) : null;
        return new String[]{lastName, firstName};
    }

    private String deriveGradeLevel(String courseNumber, String courseName) {
        String code = courseNumber == null ? "" : courseNumber.trim();
        String name = courseName == null ? "" : courseName.trim();
        if (code.endsWith("K4") || name.endsWith("K4")) {
            return "K4";
        }
        if (code.endsWith("K5") || name.endsWith("K5")) {
            return "K5";
        }
        if (code.matches(".*(?:0[1-5])$")) {
            return String.valueOf(Integer.parseInt(code.substring(code.length() - 2)));
        }
        if (code.matches(".*(?:[678])$") && (code.startsWith("PEH") || code.startsWith("Bible") || code.startsWith("C") || code.startsWith("PhySci"))) {
            return code.substring(code.length() - 1);
        }
        if (code.matches(".*(?:9|10|11|12)$") || name.startsWith("AP ") || code.startsWith("AP") || code.equalsIgnoreCase("Bio")
            || code.equalsIgnoreCase("Geo") || code.equalsIgnoreCase("Alg2") || code.equalsIgnoreCase("CHEM01")) {
            return "HS";
        }
        return null;
    }
}
