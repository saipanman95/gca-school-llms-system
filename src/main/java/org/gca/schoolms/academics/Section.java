package org.gca.schoolms.academics;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String termName;

    @Column(nullable = false)
    private String courseCode;

    @Column(nullable = false)
    private String courseTitle;

    @Column(nullable = false)
    private String teacherName;

    protected Section() {
    }

    public Section(String termName, String courseCode, String courseTitle, String teacherName) {
        this.termName = termName;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.teacherName = teacherName;
    }

    public Long getId() {
        return id;
    }

    public String getTermName() {
        return termName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public String getTeacherName() {
        return teacherName;
    }
}
