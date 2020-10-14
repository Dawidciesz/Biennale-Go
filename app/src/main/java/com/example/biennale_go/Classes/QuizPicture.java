package com.example.biennale_go.Classes;

import android.graphics.drawable.Drawable;

public class QuizPicture {
    Drawable a, b, c, d;

    public QuizPicture(Drawable a, Drawable b, Drawable c, Drawable d, String questionNumber) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        QuestionNumber = questionNumber;
    }

    public Drawable getA() {
        return a;
    }

    public void setA(Drawable a) {
        this.a = a;
    }

    public Drawable getB() {
        return b;
    }

    public void setB(Drawable b) {
        this.b = b;
    }

    public Drawable getC() {
        return c;
    }

    public void setC(Drawable c) {
        this.c = c;
    }

    public Drawable getD() {
        return d;
    }

    public void setD(Drawable d) {
        this.d = d;
    }

    public String getQuestionNumber() {
        return QuestionNumber;
    }

    public void setQuestionNumber(String questionNumber) {
        QuestionNumber = questionNumber;
    }

    String QuestionNumber;
}
