/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movierecsys.bll.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import movierecsys.be.Movie;
import movierecsys.be.Rating;
import movierecsys.be.User;
import movierecsys.dal.File.MovieDAO;
import movierecsys.dal.File.UserDAO;
import movierecsys.dal.interfaces.IMovieRepository;
import movierecsys.dal.interfaces.IUserRepository;

/**
 *
 * @author Thorbjørn Schultz Damkjær
 *
 */
public class MovieRecommender {

    private final IMovieRepository movieDAO;
    private final IUserRepository userDAO;

    public MovieRecommender() {
        movieDAO = new MovieDAO();
        userDAO = new UserDAO();
    }

    public List<Movie> highAverageRecommendations(List<Rating> ratings, List<Rating> userRatings) throws IOException {
        List<Movie> topRatedMovies = new ArrayList<>();
        List<Rating> movieRatings = new ArrayList<>();

        Collections.sort(ratings, (Rating r1, Rating r2) -> Double.compare(r2.getMovie(), r1.getMovie()));
        Rating priviuseRating = ratings.get(0);
        for (Rating rating : ratings) {
            if (rating.getMovie() != priviuseRating.getMovie()) {
                Movie movie = movieDAO.getMovie(priviuseRating.getMovie());
                setAvgRating(movieRatings, movie);
                topRatedMovies.add(movie);
                movieRatings = new ArrayList<>();
                movieRatings.add(rating);
            } else {
                movieRatings.add(rating);
            }

            priviuseRating = rating;
        }

        Collections.sort(topRatedMovies, (Movie m1, Movie m2) -> Double.compare(m2.getAvgRating2(), m1.getAvgRating2()));
        return topRatedMovies;
    }

    private void setAvgRating(List<Rating> movieRatings, Movie movie) {
        for (Rating movieRating : movieRatings) {
            movie.addtoRating(movieRating.getRating());
        }
    }

    /**
     * Returns a list of movie recommendations based on weighted
     * recommendations. Excluding already rated movies from the list of results.
     *
     * @param allRatings List of all users ratings.
     * @param excludeRatings List of Ratings (aka. movies) to exclude.
     * @return Sorted list of movies recommended to the caller. Sorted in
     * descending order.
     */
    public List<Movie> weightedRecommendations(List<Rating> ratingList, List<Rating> userRatings, User user) throws IOException {
        List<Movie> movies = movieDAO.getAllMovies(), 
                userMovies = getMovieFromList(userRatings, movies), 
                recommendedMovies = new ArrayList<>();
        
        List<Rating> pairedTempRating, 
                userTempRatings, 
                pairedUserRatings = new ArrayList<>();
        
        ratingList.removeAll(userRatings);

        Collections.sort(ratingList, (Rating r1, Rating r2) -> Double.compare(r2.getUser(), r1.getUser()));

        Rating priviuseRating = ratingList.get(0);

        for (Rating rating : ratingList) {

            if (rating.getUser() == priviuseRating.getUser()) {

                pairedUserRatings.add(rating); //adds to list that only has the same user

            } else {
                pairedTempRating = new ArrayList();
                userTempRatings = new ArrayList();

                removeNonSimilarMovies(userRatings, pairedUserRatings, pairedTempRating, userTempRatings);

                if (!pairedTempRating.isEmpty()) { // throws out the list if it doen't contain any of the same rated movie 
                    
                    List<Movie> pairRatedMovies = getMovieFromList(pairedUserRatings, movies);

                    int similarity = calculateSimilarity(pairedTempRating, userTempRatings);

                    recommendedMovies.removeAll(pairRatedMovies); //removes the movies so we doesn't get dublicates
                    
                    setRecommendationsValue(pairedUserRatings, pairRatedMovies, similarity, recommendedMovies);
                }

                pairedUserRatings = new ArrayList<>();
                pairedUserRatings.add(rating);
            }

            priviuseRating = rating;
        }
        recommendedMovies.removeAll(userMovies);
        Collections.sort(recommendedMovies, (Movie m1, Movie m2) -> Integer.compare(m2.getRecommendationValue(), m1.getRecommendationValue()));
        return recommendedMovies;
    }

    /**
     * Removes the rated movies that the loginUser And the pairedUser have not
     * int common
     *
     * @param userRatings
     * @param pairedUserRatings
     * @param pairedTemp
     * @param userTempRatings
     */
    private void removeNonSimilarMovies(List<Rating> userRatings, List<Rating> pairedUserRatings, List<Rating> pairedTemp, List<Rating> userTempRatings) {
        for (Rating userRating : userRatings) {
            for (Rating pairedUserRating : pairedUserRatings) {
                if (userRating.getMovie() == pairedUserRating.getMovie()) {
                    pairedTemp.add(pairedUserRating);
                    userTempRatings.add(userRating);
                    break;
                }
            }
        }
    }

    /**
     * gets all the movie the from a list of ratings
     *
     * @param inputRating
     * @param listOfAllMovies
     * @return
     * @throws IOException
     */
    private List<Movie> getMovieFromList(List<Rating> inputRating, List<Movie> listOfAllMovies) throws IOException {

        List<Movie> outputMovieList = new ArrayList<>();
        int i = 0;
        for (Movie movie : listOfAllMovies) {
            if (movie.getId() == inputRating.get(i).getMovie()) {
                outputMovieList.add(movie);
                i++;
                if (i >= inputRating.size()) {
                    break;
                }
            }
        }
        return outputMovieList;
    }

    /**
     * Calculate at Sets the RecommendationValue
     *
     * @param pairedUserRatings
     * @param ratedMovies
     * @param similarity
     * @param recommendedMovies
     */
    private void setRecommendationsValue(List<Rating> pairedUserRatings, List<Movie> ratedMovies, int similarity, List<Movie> recommendedMovies) {
        int i = 0;
        for (Rating rating : pairedUserRatings) {
            if (rating.getMovie() == ratedMovies.get(i).getId()) {
                ratedMovies.get(i).addToRecommendationValue(similarity * rating.getRating());
                recommendedMovies.add(ratedMovies.get(i));

            } else {
                System.out.println("Somthing went wrong when setting RecommendedValue");
                break;
            }
            i++;
            if (i >= pairedUserRatings.size()) {
                break;
            }
        }
    }

    /**
     * Calculate the Similarity between Users Ratings NEEDS TO BE RATINGS OF THE
     * SAME MOVIES
     *
     * @param pairedTemp
     * @param userTempRatings
     * @return
     */
    private int calculateSimilarity(List<Rating> pairedTemp, List<Rating> userTempRatings) {
        int i = 0;
        int similarity = 0;
        for (Rating tempRating : pairedTemp) {
            if (userTempRatings.get(i).getMovie() == tempRating.getMovie()) {
                similarity += userTempRatings.get(i).getRating() * tempRating.getRating();
            } else {
                System.out.println("Something went wrong when pairing user and Other user");
                break;
            }
            i++;
            if (i >= pairedTemp.size()) {
                break;
            }
        }
        return similarity;
    }
}
