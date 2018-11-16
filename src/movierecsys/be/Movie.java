/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movierecsys.be;

/**
 *
 * @author pgn
 */
public class Movie {

    private final int id;
    private String title;
    private int year, recommendationValue;
    private double avgRating;

    public Movie(int id, int year, String title) {
        this.id = id;
        this.title = title;
        this.year = year;
        recommendationValue = 0;
    }

    public int getRecommendationValue() {
        return recommendationValue;
    }

    public void setRecommendationValue(int recommendationValue) {
        this.recommendationValue = recommendationValue;
    }

    public Movie(int id, double avgRating) {
        this.id = id;
        this.avgRating = avgRating;
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

    public double getAvgRating() {
        return avgRating;
    }

    @Override
    public String toString() {
        return id + "," + title + "," + year;
    }

}
