package org.gca.schoolms.certificates;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class CertificateTeacherResolverService {

    public String findHomeRoomTeacher(String courseName, List<CertificateStudentCourseGrade> grades) {
        String citizenshipTeacher = grades.stream()
            .filter(grade -> courseMatches(grade.courseName(), courseName))
            .map(this::resolveTeacherDisplay)
            .filter(this::isNonBlank)
            .findFirst()
            .orElse(null);

        if (isNonBlank(citizenshipTeacher)) {
            return citizenshipTeacher;
        }

        Map<String, Integer> teacherCounts = new LinkedHashMap<>();
        for (CertificateStudentCourseGrade grade : grades) {
            String teacherDisplay = resolveTeacherDisplay(grade);
            if (!isNonBlank(teacherDisplay)) {
                continue;
            }
            teacherCounts.merge(teacherDisplay, 1, Integer::sum);
        }

        String bestTeacher = null;
        int bestCount = 0;
        for (Map.Entry<String, Integer> entry : teacherCounts.entrySet()) {
            if (entry.getValue() > bestCount) {
                bestTeacher = entry.getKey();
                bestCount = entry.getValue();
            }
        }
        return bestTeacher;
    }

    private boolean courseMatches(String actualCourseName, String expectedCourseName) {
        if (!isNonBlank(actualCourseName) || !isNonBlank(expectedCourseName)) {
            return false;
        }
        return actualCourseName.equalsIgnoreCase(expectedCourseName)
            || actualCourseName.toLowerCase().contains(expectedCourseName.toLowerCase());
    }

    private String resolveTeacherDisplay(CertificateStudentCourseGrade grade) {
        String firstName = trimToNull(grade.teacherFirstName());
        String lastName = trimToNull(grade.teacherLastName());
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        if (firstName != null) {
            return firstName;
        }
        if (lastName != null) {
            return lastName;
        }

        String teacherName = trimToNull(grade.teacherName());
        if (teacherName == null) {
            return null;
        }

        int commaIndex = teacherName.indexOf(',');
        if (commaIndex >= 0) {
            String parsedLastName = trimToNull(teacherName.substring(0, commaIndex));
            String parsedFirstName = trimToNull(teacherName.substring(commaIndex + 1));
            if (parsedFirstName != null && parsedLastName != null) {
                return parsedFirstName + " " + parsedLastName;
            }
        }
        return teacherName;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean isNonBlank(String value) {
        return trimToNull(value) != null;
    }
}
