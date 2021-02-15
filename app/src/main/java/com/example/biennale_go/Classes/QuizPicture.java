package com.example.biennale_go.Classes;

import android.graphics.Bitmap;

public class QuizPicture {
    Bitmap a, b, c, d;

    public QuizPicture(Bitmap a, Bitmap b, Bitmap c, Bitmap d, String questionNumber) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        QuestionNumber = questionNumber;
    }

    public Bitmap getA() {
        return a;
    }

    public void setA(Bitmap a) {
        this.a = a;
    }

    public Bitmap getB() {
        return b;
    }

    public void setB(Bitmap b) {
        this.b = b;
    }

    public Bitmap getC() {
        return c;
    }

    public void setC(Bitmap c) {
        this.c = c;
    }

    public Bitmap getD() {
        return d;
    }

    public void setD(Bitmap d) {
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
