package org.gca.schoolms.enrollment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import org.gca.schoolms.records.Student;

@Entity
public class EnrollmentDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "enrollment_request_id", nullable = false)
    private EnrollmentRequest enrollmentRequest;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id")
    private Student student;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentDocumentType recordType;

    @Column(nullable = false)
    private String recordName;

    @Column(nullable = false)
    private String storedFilename;

    private String contentType;

    @Column(nullable = false)
    private String storagePath;

    @Column(nullable = false)
    private LocalDateTime dateUploaded;

    protected EnrollmentDocument() {}

    public EnrollmentDocument(EnrollmentRequest enrollmentRequest, Student student, EnrollmentDocumentType recordType,
                              String recordName, String storedFilename, String contentType,
                              String storagePath, LocalDateTime dateUploaded) {
        this.enrollmentRequest = enrollmentRequest;
        this.student = student;
        this.recordType = recordType;
        this.recordName = recordName;
        this.storedFilename = storedFilename;
        this.contentType = contentType;
        this.storagePath = storagePath;
        this.dateUploaded = dateUploaded;
    }

    public EnrollmentDocumentType getRecordType() {
        return recordType;
    }

    public String getRecordName() {
        return recordName;
    }

    public String getStoredFilename() {
        return storedFilename;
    }

    public String getContentType() {
        return contentType;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public LocalDateTime getDateUploaded() {
        return dateUploaded;
    }
}
