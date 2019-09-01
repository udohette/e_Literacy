package com.example.techflex_e_literacy.quiz;

import java.util.Calendar;

public class CourseReg {
    private String studentId;
    private String courseReg;
    private String semester_count;
    private String email;
    private String startDate;
    private String endDate;

    public CourseReg(String studentId, String courseReg, String semester_count, String email, String startDate, String endDate) {
        this.studentId = studentId;
        this.courseReg = courseReg;
        this.semester_count = semester_count;
        this.email = email;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getCourseReg() {
        return courseReg;
    }

    public void setCourseReg(String courseReg) {
        this.courseReg = courseReg;
    }

    public String getSemester_count() {
        return semester_count;
    }

    public void setSemester_count(String semester_count) {
        this.semester_count = semester_count;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
