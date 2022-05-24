package com.pump.simpleandroid;
import java.util.ArrayList;
import java.util.List;
public class Question {

    private String text;
    private QuestionType type;
    private List<Answer> answers;
    private Boolean isCorrect;

    Question() {
        answers = new ArrayList<>();
        type = QuestionType.SINGLE;
        isCorrect = false;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    QuestionType getType() {
        return type;
    }

    void setType(QuestionType type) {
        this.type = type;
    }

    List<Answer> getAnswers() {
        return answers;
    }

    void newAnswer(String answerText, Boolean isCorrect) {
        Answer newAnswer = new Answer();
        newAnswer.setText(answerText);
        newAnswer.setCorrect(isCorrect);

        this.answers.add(newAnswer);
    }
    Boolean isCorrect(){
        switch (type) {
            case SINGLE:
                checkSingle();
                break;

            case MULTIPLE:
                checkMultiple();
                break;

            case FREETEXT:
                checkFreeText();
                break;
        }

        return isCorrect;
    }
    private void checkSingle() {

        for (Answer answer : answers) {
            if (answer.getSelected() && answer.getCorrect()) {
                isCorrect = true;
            }
        }
    }
    private void checkMultiple() {
        for (Answer answer : answers) {
            if (!(answer.getCorrect() == answer.getSelected())) {
                isCorrect = false;
                return;
            }
        }

        isCorrect = true;
    }
    private void checkFreeText() {
        Answer answer = answers.get(0);

        String userInput = answer.getUserFreeText();
        String correctResponse = answer.getText();

        if (userInput != null && correctResponse != null) {

            isCorrect = userInput.equals(correctResponse);
        }
    }
}
