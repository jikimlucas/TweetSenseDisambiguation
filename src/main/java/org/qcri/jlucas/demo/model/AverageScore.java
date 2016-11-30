package org.qcri.jlucas.demo.model;

/**
 * Created by jlucas on 11/11/16.
 */
public class AverageScore {
    private String name;
    private String text;
    private double score;

    public AverageScore(String name, String text,  double score) {
        this.name = name;
        this.text = text;
        this.score = score;
    }

    public AverageScore(String name, double score) {
        this.name = name;
        this.text = "";
        this.score = score;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
