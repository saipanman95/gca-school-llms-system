package org.gca.schoolms.integration.powerschool;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "teacher_course_stage", indexes = {
    @Index(name = "idx_teacher_course_stage_course", columnList = "course_name"),
    @Index(name = "idx_teacher_course_stage_school_year", columnList = "school_year")
})
public class TeacherCourseStage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "teacher_name", nullable = false)
    private String teacherName;

    @Column(name = "teacher_last_name")
    private String teacherLastName;

    @Column(name = "teacher_first_name")
    private String teacherFirstName;

    @Column(name = "expression")
    private String expression;

    @Column(name = "school_year", nullable = false)
    private String schoolYear;

    @Column(name = "course_number", nullable = false)
    private String courseNumber;

    @Column(name = "course_name", nullable = false)
    private String courseName;

    @Column(name = "section_number")
    private String sectionNumber;

    @Column(name = "room")
    private String room;

    @Column(name = "grade_level")
    private String gradeLevel;

    protected TeacherCourseStage() {
    }

    public TeacherCourseStage(String teacherName, String teacherLastName, String teacherFirstName,
                              String expression, String schoolYear, String courseNumber,
                              String courseName, String sectionNumber, String room, String gradeLevel) {
        this.teacherName = teacherName;
        this.teacherLastName = teacherLastName;
        this.teacherFirstName = teacherFirstName;
        this.expression = expression;
        this.schoolYear = schoolYear;
        this.courseNumber = courseNumber;
        this.courseName = courseName;
        this.sectionNumber = sectionNumber;
        this.room = room;
        this.gradeLevel = gradeLevel;
    }
}
