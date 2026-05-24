package org.gca.schoolms.integration.powerschool;

import java.util.List;

public record PowerSchoolImportReport(
    boolean success,
    String originalFilename,
    PowerSchoolImportDatasetType datasetType,
    int rowsProcessed,
    List<String> messages
) {
}
