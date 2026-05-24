package org.gca.schoolms.integration.powerschool;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class PowerSchoolImportRunner implements ApplicationRunner {

    private final PowerSchoolImportService importService;
    private final boolean enabled;
    private final List<String> paths;

    public PowerSchoolImportRunner(
        PowerSchoolImportService importService,
        @Value("${app.powerschool-import.enabled:false}") boolean enabled,
        @Value("${app.powerschool-import.paths:}") List<String> paths
    ) {
        this.importService = importService;
        this.enabled = enabled;
        this.paths = paths == null ? List.of() : paths.stream().filter(path -> path != null && !path.isBlank()).toList();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!enabled) {
            return;
        }
        if (paths.isEmpty()) {
            throw new IllegalStateException("PowerSchool import is enabled but no app.powerschool-import.paths were provided.");
        }

        for (String configuredPath : paths) {
            Path path = Path.of(configuredPath).toAbsolutePath().normalize();
            if (!Files.exists(path)) {
                throw new IllegalStateException("PowerSchool import file not found: " + path);
            }
            PowerSchoolImportReport report = importService.importFile(path);
            if (!report.success()) {
                throw new IllegalStateException("Failed to import " + path.getFileName() + ": " + String.join(" | ", report.messages()));
            }
            System.out.println("[PowerSchool Import] " + path.getFileName() + " -> " + report.datasetType().getDisplayName()
                + " (" + report.rowsProcessed() + " rows)");
        }
    }
}
