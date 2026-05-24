package org.gca.schoolms.integration.powerschool;

import java.util.List;

public record PowerSchoolImportPreview(
    boolean readyToImport,
    String token,
    String originalFilename,
    PowerSchoolImportDatasetType datasetType,
    int rowsValidated,
    List<String> messages
) {
}
