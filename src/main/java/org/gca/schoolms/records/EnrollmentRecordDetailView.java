package org.gca.schoolms.records;

import java.util.List;
import org.gca.schoolms.enrollment.EnrollmentDocument;
import org.gca.schoolms.enrollment.EnrollmentEmergencyContact;
import org.gca.schoolms.enrollment.EnrollmentFinanceAuthorization;
import org.gca.schoolms.enrollment.EnrollmentRequest;
import org.gca.schoolms.enrollment.EnrollmentRequestLanguage;
import org.gca.schoolms.finance.StudentFee;

public record EnrollmentRecordDetailView(
    EnrollmentRequest enrollmentRequest,
    List<EnrollmentRequestLanguage> studentLanguages,
    List<EnrollmentEmergencyContact> emergencyContacts,
    List<EnrollmentFinanceAuthorization> financeAuthorizations,
    List<EnrollmentDocument> documents,
    StudentFee enrollmentFee,
    List<String> missingDocuments
) {
}
