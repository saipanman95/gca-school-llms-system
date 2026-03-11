package org.gca.schoolms.academics;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {
    List<AttendanceRecord> findTop10ByOrderByAttendanceDateDesc();

    @Query("select count(a) from AttendanceRecord a where a.status = org.gca.schoolms.academics.AttendanceStatus.ABSENT")
    long countAbsences();
}
