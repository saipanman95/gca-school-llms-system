package org.gca.schoolms.academics;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.gca.schoolms.organization.Campus;

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

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "campus_id", nullable = false)
    private Campus campus;

    protected Section() {
    }

    public Section(String termName, String courseCode, String courseTitle, String teacherName, Campus campus) {
        this.termName = termName;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.teacherName = teacherName;
        this.campus = campus;
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

    public Campus getCampus() {
        return campus;
    }
}
