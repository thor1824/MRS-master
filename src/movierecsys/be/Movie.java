/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movierecsys.be;

import java.util.List;

/**
 *
 * @author Thorbjørn Schultz Damkjær
 */
public class Movie {

    private final int id;
    private String title;
    private int year, recommendationValue, counter;
    private double sumRating;
    private List<Rating> ratings;

    public Movie(int id, int year, String title) {
        this.id = id;
        this.title = title;
        this.year = year;
        recommendationValue = 0;
        counter = 0;
        sumRating = 0;

    }

    public double getAvgRating2() {
        return sumRating / counter;
    }
    
    public void addtoRating(double rating) {
        addToNumberOfRating();
        this.sumRating = this.sumRating + rating;
    }

    public int getRecommendationValue() {
        return recommendationValue;
    }

    public void addToRecommendationValue(int recommendationValue) {
        this.recommendationValue += recommendationValue;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String dataSignatur() {
        return id + "," + year + "," + title;
    }

    private void addToNumberOfRating() {
        counter++;
    }

    public void addToRatings(List<Rating> addetions) {
        ratings.addAll(addetions);
    }

    @Override
    public String toString() {
        return year + "      " + title;
    }

}
