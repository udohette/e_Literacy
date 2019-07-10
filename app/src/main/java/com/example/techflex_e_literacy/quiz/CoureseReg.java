package com.example.techflex_e_literacy.quiz;

public class CoureseReg {
    private String studentId;
    private String courseReg;
    private String semester_count;
    private String email;

    public CoureseReg(String studentId, String courseReg, String semester_count, String email) {
        this.studentId = studentId;
        this.courseReg = courseReg;
        this.semester_count = semester_count;
        this.email = email;
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
}
