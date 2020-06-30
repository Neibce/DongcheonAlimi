package dev.jun0.dcalimi.item;

import java.io.Serializable;

public class QuizItem implements Serializable {
    private String mQuestion;
    private String[] mOptions;

    public QuizItem(String question, String[] options){
        mQuestion = question;
        mOptions = options;
    }

    public String getQuestion() {
        return mQuestion;
    }

    public String[] getOptions(){
        return mOptions;
    }
}
