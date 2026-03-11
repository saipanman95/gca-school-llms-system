package org.gca.schoolms.enrollment;

public record StoredDocument(String storedFilename, String storagePath, String originalFilename, String contentType) {
}
