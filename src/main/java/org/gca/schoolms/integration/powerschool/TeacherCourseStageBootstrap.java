package org.gca.schoolms.integration.powerschool;

import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class TeacherCourseStageBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(TeacherCourseStageBootstrap.class);

    private final TeacherCourseStageRepository repository;
    private final TeacherCourseStageSeedService seedService;

    public TeacherCourseStageBootstrap(TeacherCourseStageRepository repository,
                                       TeacherCourseStageSeedService seedService) {
        this.repository = repository;
        this.seedService = seedService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (repository.count() > 0) {
            return;
        }

        List<TeacherCourseStage> rows = loadRows();
        if (rows.isEmpty()) {
            log.warn("Teacher course stage seed file was loaded but contained no rows.");
            return;
        }

        repository.saveAll(rows);
        log.info("Seeded {} teacher course stage rows.", rows.size());
    }

    private List<TeacherCourseStage> loadRows() {
        try {
            return seedService.loadSeedRows();
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to seed teacher course stage data", exception);
        }
    }
}
