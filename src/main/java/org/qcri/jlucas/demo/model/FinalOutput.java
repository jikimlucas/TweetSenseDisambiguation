package org.qcri.jlucas.demo.model;

/**
 * Created by jlucas on 11/14/16.
 */
public class FinalOutput {

    private String text;
    private double person_score;
    private double country_score;

    public FinalOutput(String text, double person_score, double country_score) {
        this.text = text;
        this.person_score = person_score;
        this.country_score = country_score;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getPerson_score() {
        return person_score;
    }

    public void setPerson_score(double person_score) {
        this.person_score = person_score;
    }

    public double getCountry_score() {
        return country_score;
    }

    public void setCountry_score(double country_score) {
        this.country_score = country_score;
    }
}
