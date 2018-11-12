/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movierecsys.dal;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import movierecsys.be.Movie;
import movierecsys.be.Rating;
import movierecsys.be.User;

/**
 *
 * @author pgn
 */
public class RatingDAO {

    private static final String SOURCE = "data/ratings.txt";

    /**
     * Persists the given rating.
     *
     * @param rating the rating to persist.
     */
    public void createRating(Rating rating) {

        //To DO tilføj til filen
    }

    /**
     * Updates the rating to reflect the given object.
     *
     * @param rating The updated rating to persist.
     */
    public void updateRating(Rating rating) throws IOException {
        for (Rating oldRating : getAllRatings()) {
            if (oldRating.getUser() == rating.getUser() && oldRating.getMovie() == rating.getMovie()) {
                oldRating = rating;
                return;
            }
        }
        createRating(rating);
        //To DO tilføj til filen
    }

    /**
     * Removes the given rating.
     *
     * @param rating
     */
    public void deleteRating(Rating rating) throws IOException {
        getAllRatings().remove(rating);
        //To DO Fjern fra filen
    }

    /**
     * Gets all ratings from all users.
     *
     * @return List of all ratings.
     */
    public List<Rating> getAllRatings() throws IOException {
        File file = new File(SOURCE);

        List<String> lines = Files.readAllLines(file.toPath());
        List<Rating> allRatings = new ArrayList<>();
        
        for (String line : lines) {
            allRatings.add(createRatingFromString(line));
        }
        return allRatings;

    }

    private Rating createRatingFromString(String strRating) {
        UserDAO userDAO = new UserDAO();
        MovieDAO movieD = new MovieDAO();
        
        String[] columms = strRating.split(",");
        
        int userId = Integer.parseInt(columms[0]);
        User user = userDAO.getUser(userId);
        
        int movieId = Integer.parseInt(columms[1]);
        Movie movie = movieD.getMovie(movieId);
        
        int ratingOfMovie = Integer.parseInt(columms[2]);
        Rating rating = new Rating(movie, user, ratingOfMovie);
        return rating;
    }

    /**
     * Get all ratings from a specific user.
     *
     * @param user The user
     * @return The list of ratings.
     */
    public List<Rating> getRatings(User user) throws IOException {
        List<Rating> ratingsOfUser = new ArrayList<>();
        for (Rating rating : getAllRatings()) {
            if (user.getId() == rating.getUser().getId()) {
                ratingsOfUser.add(rating);
            }
        }
        return ratingsOfUser;
    }

}
