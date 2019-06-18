package com.example.techflex_e_literacy.quiz;

import java.io.Serializable;

public class QuestionLibraryFBQ implements Serializable {
    public String question, answer;

    public QuestionLibraryFBQ(){};

    public QuestionLibraryFBQ(String questions, String answer) {
        this.question = questions;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestions(String questions) {
        this.question = questions;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
