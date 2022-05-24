package com.pump.simpleandroid;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public LinearLayout answerLayout;

    public List<Question> questions;
    public int activeQuestion;

    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        answerLayout = findViewById(R.id.answer_layout);

        startNewQuiz(null);
    }

    public void startNewQuiz(View view) {
        loadData();
        activeQuestion = 0;
        displayInitialView(activeQuestion);
    }

    private void displayInitialView(int questionIndex) {
        Question question = questions.get(questionIndex);

        displayQuestion(question.getText());
        displayPossibleAnswers(question.getAnswers(), question.getType());
        updateProgressText();
        setInitialViewVisibility();
    }

    private void setInitialViewVisibility() {
        setVisibility(R.id.progress_text, View.VISIBLE);
        setVisibility(R.id.previous_question_button, View.INVISIBLE);
        setVisibility(R.id.next_question_button, View.VISIBLE);
        setVisibility(R.id.finish_button, View.GONE);
        setVisibility(R.id.restart_button, View.GONE);
        setVisibility(R.id.summary, View.GONE);
    }

    private void displayQuestion(String text) {
        TextView questionView = findViewById(R.id.question_text);
        questionView.setText(text);
    }

    private void displayPossibleAnswers(List<Answer> answers, QuestionType questionType) {
        switch (questionType) {
            case SINGLE:
                displaySingleTypeAnswers(answers);
                break;

            case MULTIPLE:
                displayMultipleTypeAnswers(answers);
                break;

            case FREETEXT:
                displayFreeTextTypeAnswers(answers);
                break;
        }
    }
    private void updateProgressText() {
        TextView progressText = findViewById(R.id.progress_text);

        progressText.setText(getString(R.string.progress_text,
                activeQuestion + 1, questions.size()));
    }
    private void displaySingleTypeAnswers(List<Answer> answers) {
        // needed to make enforce the user to select only one option
        RadioGroup radioGroup = new RadioGroup(this);

        answerLayout.removeAllViews();

        for (Answer answer : answers) {
            RadioButton radioButton = new RadioButton(this);

            int padding = dpToPx(8);
            radioButton.setPadding(padding, padding, padding, padding);

            int id = View.generateViewId();
            answer.setId(id);
            radioButton.setId(id);

            radioButton.setTextAppearance(this, R.style.Answer);
            radioButton.setText(answer.getText());

            radioButton.setOnClickListener(this::radioButtonChecked);

            radioButton.setChecked(answer.getSelected());

            radioGroup.addView(radioButton);
        }

        answerLayout.addView(radioGroup);
    }

    private void displayMultipleTypeAnswers(List<Answer> answers) {
        answerLayout.removeAllViews();

        for (Answer answer : answers) {
            CheckBox checkBox = new CheckBox(this);

            int padding = dpToPx(8);
            checkBox.setPadding(padding, padding, padding, padding);

            int id = View.generateViewId();
            answer.setId(id);
            checkBox.setId(id);

            checkBox.setTextAppearance(this, R.style.Answer);
            checkBox.setText(answer.getText());

            checkBox.setOnClickListener(this::checkBoxChecked);

            checkBox.setChecked(answer.getSelected());

            answerLayout.addView(checkBox);
        }
    }

    private void displayFreeTextTypeAnswers(List<Answer> answers) {
        answerLayout.removeAllViews();

        Answer answer = answers.get(0);

        EditText editText = new EditText(this);

        int id = View.generateViewId();
        answer.setId(id);
        editText.setId(id);

        editText.setTextAppearance(this, R.style.Answer);
        editText.setHint(getText(R.string.free_text_answer_hint));

        editText.setText(answer.getUserFreeText());

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                updateFreeText(s.toString());
            }
        });

        answerLayout.addView(editText);
    }

    private void radioButtonChecked(View view) {
        List<Answer> answers = questions.get(activeQuestion).getAnswers();

        for (Answer answer : answers) {
            answer.setSelected(answer.getId() == view.getId());
        }
    }

    private void checkBoxChecked(View view) {
        List<Answer> answers = questions.get(activeQuestion).getAnswers();

        for (Answer answer : answers) {

            if (answer.getId() == view.getId()) {
                answer.setSelected(((CheckBox) view).isChecked());
            }
        }
    }

    private void updateFreeText(String userInput) {
        List<Answer> answers = questions.get(activeQuestion).getAnswers();

        for (Answer answer : answers) {
            answer.setUserFreeText(userInput);
        }
    }

    public int checkAnswers() {
        int correctCount = 0;

        for (Question question : questions) {
            if (question.isCorrect()) {
                correctCount++;
            }
        }

        showToast(correctCount + " out of " + questions.size());

        return correctCount;
    }

    public void nextQuestion(View view) {
        if (activeQuestion < questions.size() - 1) {
            answerLayout.removeAllViews();
            activeQuestion++;
            displayInitialView(activeQuestion);

            setVisibility(R.id.previous_question_button, View.VISIBLE);

        }

        if (activeQuestion == questions.size() - 1) {
            setVisibility(R.id.next_question_button, View.GONE);

            setVisibility(R.id.finish_button, View.VISIBLE);
        }
    }
    public void prevQuestion(View view) {
        if (activeQuestion > 0) {
            answerLayout.removeAllViews();
            activeQuestion--;
            displayInitialView(activeQuestion);

            setVisibility(R.id.previous_question_button, View.VISIBLE);

            setVisibility(R.id.next_question_button, View.VISIBLE);

            setVisibility(R.id.finish_button, View.GONE);
        }

        if (activeQuestion == 0) {
            setVisibility(R.id.previous_question_button, View.INVISIBLE);
        }
    }

    private void setVisibility(int viewId, int visibility) {
        View view = findViewById(viewId);
        view.setVisibility(visibility);
    }

    public void finishQuiz(View view) {
        activeQuestion++;

        setVisibility(R.id.finish_button, View.GONE);

        setVisibility(R.id.previous_question_button, View.GONE);

        setVisibility(R.id.restart_button, View.VISIBLE);

        setVisibility(R.id.progress_text, View.GONE);

        answerLayout.removeAllViews();

        displaySummary();
    }

    private void displaySummary() {
        int correctCount = checkAnswers();

        String finishMsg;

        if (correctCount < 3) {
            finishMsg = getString(R.string.finish_msg_1);
        } else if (correctCount < 6) {
            finishMsg = getString(R.string.finish_msg_2);
        } else if (correctCount < 9) {
            finishMsg = getString(R.string.finish_msg_3);
        } else {
            finishMsg = getString(R.string.finish_msg_4);
        }

        finishMsg += "\n" + correctCount + " " + getString(R.string.out_of) + " " + questions.size();

        TextView finishText = findViewById(R.id.question_text);
        finishText.setText(finishMsg);

        LinearLayout summary = findViewById(R.id.summary);
        summary.removeAllViews();
        setVisibility(R.id.summary, View.VISIBLE);

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);

            TextView text = new TextView(this, null, R.style.FullWidth);
            int padding = dpToPx(8);
            text.setPadding(padding, padding, padding, padding);

            String feedback = getString(R.string.question) + " " + (i + 1) + "\t\t\t";
            feedback += question.isCorrect() ? getString(R.string.correct) : getString(R.string.incorrect);

            text.setText(feedback);
            summary.addView(text);
        }

    }

    private int dpToPx(int paddingDp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (paddingDp * scale + 0.5f);
    }

    private void showToast(String msg) {
        if (toast != null) {
            toast.cancel();
        }

        toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void loadData() {
        this.questions = new ArrayList<>();

        Question q1 = new Question();
        q1.setText(getString(R.string.q1));
        q1.setType(QuestionType.SINGLE);
        q1.newAnswer(getString(R.string.q1a1), false);
        q1.newAnswer(getString(R.string.q1a2), false);
        q1.newAnswer(getString(R.string.q1a3), true);
        q1.newAnswer(getString(R.string.q1a4), false);
        questions.add(q1);

        Question q2 = new Question();
        q2.setText(getString(R.string.q2));
        q2.setType(QuestionType.MULTIPLE);
        q2.newAnswer(getString(R.string.q2a1), false);
        q2.newAnswer(getString(R.string.q2a2), true);
        q2.newAnswer(getString(R.string.q2a3), false);
        q2.newAnswer(getString(R.string.q2a4), true);
        questions.add(q2);

        Question q3 = new Question();
        q3.setText(getString(R.string.q3));
        q3.setType(QuestionType.SINGLE);
        q3.newAnswer(getString(R.string.q3a1), false);
        q3.newAnswer(getString(R.string.q3a2), false);
        q3.newAnswer(getString(R.string.q3a3), true);
        q3.newAnswer(getString(R.string.q3a4), false);
        questions.add(q3);

        Question q4 = new Question();
        q4.setText(getString(R.string.q4));
        q4.setType(QuestionType.MULTIPLE);
        q4.newAnswer(getString(R.string.q4a1), true);
        q4.newAnswer(getString(R.string.q4a2), false);
        q4.newAnswer(getString(R.string.q4a3), true);
        q4.newAnswer(getString(R.string.q4a4), false);
        questions.add(q4);

        Question q5 = new Question();
        q5.setText(getString(R.string.q5));
        q5.setType(QuestionType.FREETEXT);
        q5.newAnswer(getString(R.string.q5a1), true);
        questions.add(q5);

        Question q6 = new Question();
        q6.setText(getString(R.string.q6));
        q6.setType(QuestionType.SINGLE);
        q6.newAnswer(getString(R.string.q6a1), false);
        q6.newAnswer(getString(R.string.q6a2), true);
        questions.add(q6);

        Question q7 = new Question();
        q7.setText(getString(R.string.q7));
        q7.setType(QuestionType.FREETEXT);
        q7.newAnswer(getString(R.string.q7a1), true);
        questions.add(q7);

        Question q8 = new Question();
        q8.setText(getString(R.string.q8));
        q8.setType(QuestionType.SINGLE);
        q8.newAnswer(getString(R.string.q8a1), false);
        q8.newAnswer(getString(R.string.q8a2), true);
        questions.add(q8);

        Question q9 = new Question();
        q9.setText(getString(R.string.q9));
        q9.setType(QuestionType.MULTIPLE);
        q9.newAnswer(getString(R.string.q9a1), true);
        q9.newAnswer(getString(R.string.q9a2), true);
        q9.newAnswer(getString(R.string.q9a3), false);
        q9.newAnswer(getString(R.string.q9a4), false);
        questions.add(q9);
    }
}
