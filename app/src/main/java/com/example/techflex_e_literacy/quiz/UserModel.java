package com.example.techflex_e_literacy.quiz;


public  class UserModel {
    public String quiz;
    boolean isSelected;
    public String semester;

    public UserModel(){

    }


    public UserModel(String quiz) {
        this.quiz = quiz;

    }


    public UserModel(String quiz, boolean isSelected, String semester) {
        this.quiz = quiz;
        this.isSelected = isSelected;
        this.semester = semester;
    }

    public UserModel(boolean isSelected, String semester){
        this.isSelected = isSelected;
        this.semester = semester;
    }

    public String getQuiz() {
        return quiz;
    }

    public void setQuiz(String quiz) {
        this.quiz = quiz;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }
}
