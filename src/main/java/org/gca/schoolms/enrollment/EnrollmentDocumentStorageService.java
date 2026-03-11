package org.gca.schoolms.enrollment;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EnrollmentDocumentStorageService {

    private final Path storageRoot;

    public EnrollmentDocumentStorageService(@Value("${app.storage.enrollment-root:./data/uploads/enrollment}") String storageRoot) {
        this.storageRoot = Path.of(storageRoot).toAbsolutePath().normalize();
    }

    public StoredDocument store(EnrollmentRequest enrollmentRequest, EnrollmentDocumentType documentType, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            Path requestDirectory = storageRoot.resolve(String.valueOf(enrollmentRequest.getId()));
            Files.createDirectories(requestDirectory);
            String originalFilename = file.getOriginalFilename() == null ? documentType.getStorageCode() : Path.of(file.getOriginalFilename()).getFileName().toString();
            String extension = extractExtension(originalFilename);
            String studentId = enrollmentRequest.getStudent() != null && enrollmentRequest.getStudent().getStudentNumber() != null
                ? enrollmentRequest.getStudent().getStudentNumber()
                : "pending";
            String storedFilename = "%s_%s_%s_%s_%s%s".formatted(
                sanitize(enrollmentRequest.getStudentLastName()),
                sanitize(enrollmentRequest.getStudentFirstName()),
                sanitize(studentId),
                sanitize(enrollmentRequest.getSchoolYear()),
                documentType.getStorageCode(),
                extension
            );
            Path storedPath = requestDirectory.resolve(storedFilename);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, storedPath, StandardCopyOption.REPLACE_EXISTING);
            }
            return new StoredDocument(storedFilename, storedPath.toString(), originalFilename, file.getContentType());
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to store enrollment document", ex);
        }
    }

    private String extractExtension(String originalFilename) {
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex < 0) {
            return "";
        }
        return originalFilename.substring(dotIndex).toLowerCase(Locale.ROOT);
    }

    private String sanitize(String value) {
        if (value == null || value.isBlank()) {
            return "unknown";
        }
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "");
        return normalized.toLowerCase(Locale.ROOT)
            .replaceAll("[^a-z0-9]+", "_")
            .replaceAll("^_+|_+$", "");
    }
}
