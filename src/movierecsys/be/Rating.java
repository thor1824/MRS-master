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
public class Rating {

    public static final int SUPER_BAD = -5;
    public static final int BAD = -3;
    public static final int NEUTRAL = 1;
    public static final int GOOD = 3;
    public static final int SUPER_GOOD = 5;

    
    private int rating, movieId, userId;

    /**
     * Constructs a new rating.
     *
     * @param movie The movie being rated.
     * @param user The rating user.
     * @param rating The value of the rating. Only the constants of the Rating
     * class are allowed values.
     */
    public Rating(int movieId, int userId, int rating) {
        this.movieId = movieId;
        this.userId = userId;
        this.rating = rating;
    }

    /**
     * Sets the rating to a new value. Only the constants of the Rating class
     * are allowed values.
     *
     * @param rating The rating to set.
     */
    public void setRating(int rating) {
        if (isLegalRating(rating)) {
            this.rating = rating;
        }

    }

    private boolean isLegalRating(int rating) {
        if (rating == -5 || rating == - 3 || rating == 1 || rating == 3 || rating == 5) {
            return true;
        } else {
            
        }
        System.out.println("Choose one of the folloing scores:" + " " + SUPER_BAD + " " + BAD + " " + NEUTRAL + " " + GOOD + " " + SUPER_GOOD);
        return false;

    }

    /**
     * Gets the movie being rated.
     *
     * @return A movie
     */
    public int getMovie() {
        return movieId;
    }

    /**
     * Gets the rating user.
     *
     * @return A user.
     */
    public int getUser() {
        return userId;
    }

    /**
     * Gets the rating value.
     *
     * @return An integer.
     */
    public int getRating() {
        return rating;
    }

    @Override
    public String toString() {
        return getMovie() + "," + getUser()+ "," + rating;
    }

}
