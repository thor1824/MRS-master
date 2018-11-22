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
        for (Movie topRatedMovy : topRatedMovies) {
            System.out.println(topRatedMovy.getAvgRating2()+ "    " + topRatedMovy.getTitle());
        }
        
        return topRatedMovies;
    }

    private void setAvgRating(List<Rating> movieRatings, Movie movie) {
        for (Rating movieRating : movieRatings) {
            movie.setRating(movie.getRating() + movieRating.getRating());
            movie.countUp();
            
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
        List<Movie> movies = movieDAO.getAllMovies();
        List<User> pairedUsers = userDAO.getAllUsers();
        List<Movie> reccommendedMovies = new ArrayList<>();
        List<Rating> pairedUserRatings = new ArrayList<>();
        List<Rating> pairedTemp;
        List<Rating> userTempRatings;
        pairedUsers.remove(user);
        ratingList.removeAll(userRatings);
        Collections.sort(ratingList, (Rating r1, Rating r2) -> Double.compare(r2.getUser(), r1.getUser()));
        Rating priviuseRating = new Rating(0, 0, 0);

        for (Rating rating : ratingList) {

            if (rating.getUser() != priviuseRating.getUser()) {
                pairedTemp = new ArrayList();
                userTempRatings = new ArrayList();
                removeNonSimilarMovies(userRatings, pairedUserRatings, pairedTemp, userTempRatings);

                if (!pairedTemp.isEmpty()) {
                    User pairedUser = getUserFromList(priviuseRating.getUser(), pairedUsers);
                    List<Movie> ratedMovies = movieListfromRatings(pairedUserRatings, movies);
                    int similarity = calculateSimilarity(pairedTemp, userTempRatings);
                    pairedUser.setSimilarity(similarity);
                    reccommendedMovies.removeAll(ratedMovies);
                    setRecommendationsValue(pairedUserRatings, ratedMovies, similarity, reccommendedMovies);
                }

                pairedUserRatings = new ArrayList<>();
                pairedUserRatings.add(rating);

            } else {
                pairedUserRatings.add(rating);
            }

            priviuseRating = rating;
        }

        Collections.sort(reccommendedMovies, (Movie m1, Movie m2) -> Integer.compare(m2.getRecommendationValue(), m1.getRecommendationValue()));
        System.out.println("done");
        return reccommendedMovies;
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
    private List<Movie> movieListfromRatings(List<Rating> inputRating, List<Movie> listOfAllMovies) throws IOException {

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
     * @param reccommendedMovies
     */
    private void setRecommendationsValue(List<Rating> pairedUserRatings, List<Movie> ratedMovies, int similarity, List<Movie> reccommendedMovies) {
        int i = 0;
        for (Rating recommedMovieRatings : pairedUserRatings) {
            if (recommedMovieRatings.getMovie() == ratedMovies.get(i).getId()) {
                ratedMovies.get(i).setRecommendationValue(ratedMovies.get(i).getRecommendationValue() + similarity * recommedMovieRatings.getRating());
                reccommendedMovies.add(0, ratedMovies.get(i));

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

    public User getUserFromList(int id, List<User> users) throws IOException {
        for (User user : users) {
            if (user.getId() == id) {
                return user;
            }

        }
        return null;
    }
}
