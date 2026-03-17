package org.gca.schoolms.enrollment;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentDocumentRepository extends JpaRepository<EnrollmentDocument, Long> {
    void deleteByEnrollmentRequest(EnrollmentRequest enrollmentRequest);
    List<EnrollmentDocument> findByEnrollmentRequest(EnrollmentRequest enrollmentRequest);
    List<EnrollmentDocument> findByEnrollmentRequestOrderByDateUploadedDesc(EnrollmentRequest enrollmentRequest);
}
