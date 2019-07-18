package com.example.techflex_e_literacy.quiz;

public class NumberOfQuestion {
    private String id;
    private int number_of_question_answered;

    public NumberOfQuestion(int number_of_question_answered, String id) {
        this.number_of_question_answered = number_of_question_answered;
        this.id = id;
    }

    public int getNumber_of_question_answered() {
        return number_of_question_answered;
    }

    public void setNumber_of_question_answered(int number_of_question_answered) {
        this.number_of_question_answered = number_of_question_answered;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
