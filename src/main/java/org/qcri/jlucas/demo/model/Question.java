package org.qcri.jlucas.demo.model;

/**
 * Created by jlucas on 11/14/16.
 */
public class Question {
    private String text;

    public Question(){}

    public Question(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
