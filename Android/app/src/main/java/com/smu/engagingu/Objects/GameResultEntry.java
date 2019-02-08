package com.smu.engagingu.Objects;

import java.io.Serializable;

public class GameResultEntry implements Serializable {
    private String QuizNumber;
    private String Question;
    private String Answer;
    private String UserAnswer;
    public GameResultEntry(String QuizNumber, String Question, String Answer, String UserAnswer) {
        this.QuizNumber = QuizNumber;
        this.Question = Question;
        this.Answer = Answer;
        this.UserAnswer = UserAnswer;
    }

    public String getQuizNumber() {
        return QuizNumber;
    }

    public String getUserAnswer() {
        return UserAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        UserAnswer = userAnswer;
    }

    public void setQuizNumber(String quizNumber) {
        QuizNumber = quizNumber;
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
