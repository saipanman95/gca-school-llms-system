package org.gca.schoolms.certificates;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.gca.schoolms.policy.LegacyGradingScale;
import org.gca.schoolms.policy.LegacyGradingScaleRepository;
import org.springframework.stereotype.Service;

@Service
public class CertificateLetterGradeService {

    private final LegacyGradingScaleRepository legacyGradingScaleRepository;

    public CertificateLetterGradeService(LegacyGradingScaleRepository legacyGradingScaleRepository) {
        this.legacyGradingScaleRepository = legacyGradingScaleRepository;
    }

    public List<CertificateStudentCourseGrade> transformForDisplay(List<CertificateStudentCourseGrade> grades) {
        List<LegacyGradingScale> gradingScales = legacyGradingScaleRepository.findAll().stream()
            .sorted(Comparator.comparingInt(LegacyGradingScale::getDisplayOrder))
            .toList();

        List<CertificateStudentCourseGrade> result = new ArrayList<>();
        for (CertificateStudentCourseGrade grade : grades) {
            String displayLetter = grade.letterGrade();
            if (isNumeric(displayLetter)) {
                displayLetter = resolveLetterFromPercent(grade.percent(), gradingScales);
            }
            result.add(new CertificateStudentCourseGrade(
                grade.studentId(),
                grade.studentLName(),
                grade.studentFName(),
                grade.studentMName(),
                grade.gradeLevel(),
                grade.courseName(),
                grade.percent(),
                displayLetter,
                grade.excludeFromGpa(),
                grade.teacherId(),
                grade.teacherName(),
                grade.teacherLastName(),
                grade.teacherFirstName(),
                grade.gpaPoints(),
                grade.gradeScaleName(),
                grade.academicTrack(),
                grade.computedGpa()
            ));
        }
        return result;
    }

    private boolean isNumeric(String value) {
        return value != null && value.matches("\\d+(\\.\\d+)?");
    }

    private String resolveLetterFromPercent(BigDecimal percent, List<LegacyGradingScale> gradingScales) {
        if (percent == null) {
            return "";
        }
        for (LegacyGradingScale scale : gradingScales) {
            if (percent.compareTo(scale.getMinPercent()) >= 0 && percent.compareTo(scale.getMaxPercent()) <= 0) {
                return scale.getGradeCode();
            }
        }
        return "";
    }
}
