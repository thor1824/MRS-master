/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movierecsys.be;

import java.util.List;
import java.util.Vector;

/**
 *
 * @author pgn
 */
public class Movie {

    private final int id;
    private String title;
    private int year, recommendationValue;
    private double avgRating;
    private Vector<Integer> ratings;

    public Movie(int id, int year, String title) {
        this.id = id;
        this.title = title;
        this.year = year;
        recommendationValue = 0;
        ratings = new Vector<>();

    }

    public double getAvgRating() {
        if (ratings.size() > 0) {
            int avg = 0;
            for (Integer rating : ratings) {
                avg += rating;
            }
            return avg / ratings.size();

        }
        return 0;
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

    public void addToRatings(int rating) {
        ratings.add(rating);
    }

    @Override
    public String toString() {
        return id + "," + title + "," + year;
    }

}
