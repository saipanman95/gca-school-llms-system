package org.gca.schoolms.enrollment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentDocumentRepository extends JpaRepository<EnrollmentDocument, Long> {
    void deleteByEnrollmentRequest(EnrollmentRequest enrollmentRequest);
}
