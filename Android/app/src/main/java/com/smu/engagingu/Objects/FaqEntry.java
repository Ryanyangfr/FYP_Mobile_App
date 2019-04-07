package com.smu.engagingu.Objects;

import java.io.Serializable;
/*
 * FaqEntry is used to store all information pertaining to each FAQ question and answer entry
 */
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
