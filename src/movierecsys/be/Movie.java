/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movierecsys.be;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author Thorbjørn Schultz Damkjær
 */
public class Movie {

    private final int id;
    private String title;
    private int year, recommendationValue, counter;
    private double avgRating, rating;
    private List<Rating> ratings;

    public Movie(int id, int year, String title) {
        this.id = id;
        this.title = title;
        this.year = year;
        recommendationValue = 0;
        ratings = new ArrayList<>();
        counter = 0;
        rating = 0;

    }

    public double getAvgRating() {
        if (ratings.size() > 0) {
            double avg = 0;
            for (Rating rating : ratings) {
                avg += rating.getRating();
            }
            return avg / ratings.size() - 1;

        }
        return 0;
    }

    public double getAvgRating2() {
        return rating / counter;
    }

    public double getRating() {
        return rating;
    }

    public void addtoRating(double rating) {
        addToNumberOfRating();
        this.rating = this.rating + rating;
    }

    public int getRecommendationValue() {
        return recommendationValue;
    }

    public void setRecommendationValue(int recommendationValue) {
        this.recommendationValue = recommendationValue;
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
