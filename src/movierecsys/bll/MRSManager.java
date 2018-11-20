/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movierecsys.bll;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import movierecsys.be.Movie;
import movierecsys.be.Rating;
import movierecsys.be.User;
import movierecsys.bll.exception.MovieRecSysException;
import movierecsys.dal.File.MovieDAO;
import movierecsys.dal.File.RatingDAO;
import movierecsys.dal.File.UserDAO;
import movierecsys.dal.interfaces.IMovieRepository;
import movierecsys.dal.interfaces.IRatingRepository;
import movierecsys.dal.interfaces.IUserRepository;

/**
 *
 * @author pgn
 */
public class MRSManager implements MRSLogicFacade {

    private final IMovieRepository movieDAO;
    private final IUserRepository userDAO;
    private final IRatingRepository ratingDAO;

    public MRSManager() {

        movieDAO = new MovieDAO();
        ratingDAO = new RatingDAO();
        userDAO = new UserDAO();

    }

    @Override
    public List<Rating> getRatedMovies(User user) {
        return ratingDAO.getRatings(user);
    }

    @Override
    public List<Movie> getAllTimeTopRatedMovies() throws IOException {
        List<Movie> topRatedMovies = new ArrayList<>();

        List<Movie> movies = movieDAO.getAllMovies();
        List<Rating> ratings = ratingDAO.getAllRatings();
        List<Rating> movieRatings = new ArrayList<>();

        Rating priviuseRating = new Rating(0, 0, 0);

        for (Rating rating : ratings) {
            if (rating.getMovie() != priviuseRating.getMovie()) {
                Movie movie = movieDAO.getMovie(priviuseRating.getMovie());
                movie.addToRatings(movieRatings);
                topRatedMovies.add(movie);
                movieRatings = new ArrayList<>();
                movieRatings.add(rating);
            } else {
                movieRatings.add(rating);
            }

            priviuseRating = rating;
        }

        Collections.sort(topRatedMovies, (Movie m1, Movie m2) -> Double.compare(m2.getAvgRating(), m1.getAvgRating()));

        return topRatedMovies;
    }

    @Override
    public List<Movie> getMovieReccomendations(User user) throws IOException {
        List<Rating> ratinglist = ratingDAO.getAllRatings();
        List<Rating> userRatings = ratingDAO.getRatings(user);
        List<Movie> movies = movieDAO.getAllMovies();
        List<User> pairedUsers = userDAO.getAllUsers();
        List<Movie> reccommendedMovies = new ArrayList<>();
        List<Rating> pairedUserRatings = new ArrayList<>();
        List<Rating> pairedTemp;
        List<Rating> userTempRatings;
        pairedUsers.remove(user);
        ratinglist.removeAll(userRatings);
        Collections.sort(ratinglist, (Rating r1, Rating r2) -> Double.compare(r2.getUser(), r1.getUser()));
        Rating priviuseRating = new Rating(0, 0, 0);

        for (Rating rating : ratinglist) {

            if (rating.getUser() != priviuseRating.getUser()) {
                pairedTemp = new ArrayList();
                userTempRatings = new ArrayList();
                removeNonSimilarMovies(userRatings, pairedUserRatings, pairedTemp, userTempRatings);

                if (!pairedTemp.isEmpty()) {
                    User pairedUser = userDAO.getUser(priviuseRating.getUser());
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
     * Removes the rated movies that the loginUser And the pairedUser have not int common
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
     * Calculate the Similarity between Users Ratings
     * NEEDS TO BE RATINGS OF THE SAME MOVIES  
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

    @Override
    public List<Movie> searchMovies(String query) {
        List<Movie> seachList = new ArrayList<>();
        try {
            List<Movie> movieList = movieDAO.getAllMovies();
            for (Movie movie : movieList) {
                if (movie.getTitle().toLowerCase().contains(query)) {
                    seachList.add(movie);
                }
            }
        } catch (IOException ex) {
            throw new IllegalArgumentException("Could not finde movie");
        }
        return seachList;
    }

    @Override
    public Movie createMovie(int year, String title) {

        try {
            return movieDAO.createMovie(year, title);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Could not create movie");
        }

    }

    @Override
    public void updateMovie(Movie movie) {
        try {
            movieDAO.updateMovie(movie);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Could not update movie");
        }
    }

    @Override
    public void deleteMovie(Movie movie) {

        try {
            movieDAO.deleteMovie(movie);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Could not delete movie");
        }
    }

    @Override
    public void rateMovie(int movieId, int userId, int rating) {
        try {
            Rating movieRating = new Rating(movieId, userId, rating);
            if (!ratingDAO.updateRating(movieRating)) {
                ratingDAO.createRating(movieRating);
            }
        } catch (IOException ex) {
            throw new IllegalArgumentException("Could not rate movie");
        }
    }

    @Override
    public User createNewUser(String name
    ) {
        try {
            return userDAO.createUser(name);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Could not create user");
        }
    }

    @Override
    public User getUserById(int id
    ) {
        try {
            return userDAO.getUser(id);
        } catch (IOException ex) {
            throw new IllegalArgumentException("User not found");
        }
    }

    @Override
    public List<User> getAllUsers() {
        try {
            return userDAO.getAllUsers();
        } catch (IOException ex) {
            throw new IllegalArgumentException("Could not get list of users");
        }
    }

    /**
     * Gets all movies.
     *
     * @return List of movies.
     * @throws MovieRecSysException
     */
    @Override
    public List<Movie> getAllMovies() throws MovieRecSysException {
        try {
            return movieDAO.getAllMovies();
        } catch (IOException ex) {
//            Logger.getLogger(MRSManager.class.getName()).log(Level.SEVERE, null, ex); You could log an exception
            throw new MovieRecSysException("Could not read all movies. Cause: " + ex.getMessage());
        }
    }

}
