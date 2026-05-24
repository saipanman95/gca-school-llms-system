package org.gca.schoolms.reports;

import java.util.List;
import org.gca.schoolms.certificates.CertificateStudentSummary;

public record ReportCardView(
    CertificateStudentSummary student,
    List<String> visibleTerms,
    List<ReportCardCourseRow> courses,
    StudentLegacyContactRow legacyContact,
    List<ReportCardMetricSummary> quarterSummaries,
    ReportCardMetricSummary overallSummary
) {
}
