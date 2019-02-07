package com.smu.engagingu;

import java.io.Serializable;

public class FaqEntry implements Serializable {
    private String Question;
    private String Answer;

    public FaqEntry(String question, String answer) {
        Question = question;
        Answer = answer;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public String getAnswer() {
        return Answer;
    }

    public void setAnswer(String answer) {
        Answer = answer;
    }
}
