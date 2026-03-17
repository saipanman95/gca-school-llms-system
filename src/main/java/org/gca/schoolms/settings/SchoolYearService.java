package org.gca.schoolms.settings;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SchoolYearService {

    private final SchoolYearRepository schoolYearRepository;

    public SchoolYearService(SchoolYearRepository schoolYearRepository) {
        this.schoolYearRepository = schoolYearRepository;
    }

    @Transactional(readOnly = true)
    public List<SchoolYear> schoolYears() {
        return schoolYearRepository.findAllByOrderByStartDateDesc();
    }

    @Transactional(readOnly = true)
    public String currentSchoolYearLabel() {
        LocalDate today = LocalDate.now();
        return schoolYearRepository.findAllByOrderByStartDateDesc().stream()
            .filter(schoolYear -> !today.isBefore(schoolYear.getStartDate()) && !today.isAfter(schoolYear.getEndDate()))
            .findFirst()
            .or(() -> schoolYearRepository.findAllByOrderByStartDateDesc().stream().findFirst())
            .map(SchoolYear::getLabel)
            .orElse("");
    }
}
