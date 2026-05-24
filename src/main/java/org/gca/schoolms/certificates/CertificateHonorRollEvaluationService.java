package org.gca.schoolms.certificates;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CertificateHonorRollEvaluationService {

    public HonorRollResult evaluate(List<CertificateStudentCourseGrade> grades) {
        if (grades == null || grades.isEmpty()) {
            return HonorRollResult.NONE;
        }

        BigDecimal minimum = grades.stream()
            .filter(grade -> !Boolean.TRUE.equals(grade.excludeFromGpa()))
            .map(CertificateStudentCourseGrade::percent)
            .min(BigDecimal::compareTo)
            .orElse(BigDecimal.ZERO);

        if (minimum.compareTo(BigDecimal.valueOf(95)) >= 0) {
            return HonorRollResult.PRINCIPAL;
        }
        if (minimum.compareTo(BigDecimal.valueOf(90)) >= 0) {
            return HonorRollResult.A_HONOR;
        }
        if (minimum.compareTo(BigDecimal.valueOf(80)) >= 0) {
            return HonorRollResult.B_HONOR;
        }
        return HonorRollResult.NONE;
    }
}
