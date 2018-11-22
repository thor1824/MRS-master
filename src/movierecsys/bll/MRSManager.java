/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movierecsys.bll;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import movierecsys.be.Movie;
import movierecsys.be.Rating;
import movierecsys.be.User;
import movierecsys.bll.exception.MovieRecSysException;
import movierecsys.bll.util.MovieRecommender;
import movierecsys.dal.File.MovieDAO;
import movierecsys.dal.File.RatingDAO;
import movierecsys.dal.File.UserDAO;
import movierecsys.dal.interfaces.IMovieRepository;
import movierecsys.dal.interfaces.IRatingRepository;
import movierecsys.dal.interfaces.IUserRepository;

/**
 *
 * @author Thorbjørn Schultz Damkjær
 */
public class MRSManager implements MRSLogicFacade {

    private final IMovieRepository movieDAO;
    private final IUserRepository userDAO;
    private final IRatingRepository ratingDAO;
    private final MovieRecommender movieRecommender;

    public MRSManager() {

        movieDAO = new MovieDAO();
        ratingDAO = new RatingDAO();
        userDAO = new UserDAO();
        movieRecommender = new MovieRecommender();

    }

    @Override
    public List<Rating> getRatedMovies(User user) throws IOException {
        return ratingDAO.getRatings(user);
    }

    @Override
    public List<Movie> getAllTimeTopRatedMovies(User user) throws IOException {
        List<Rating> ratings = ratingDAO.getAllRatings();
        List<Rating> userRatings = ratingDAO.getRatings(user);
        return movieRecommender.highAverageRecommendations(ratings, userRatings);
    }

    @Override
    public List<Movie> getMovieReccomendations(User user) throws IOException {
        List<Rating> ratinglist = ratingDAO.getAllRatings();
        List<Rating> userRatings = ratingDAO.getRatings(user);
        return movieRecommender.weightedRecommendations(ratinglist, userRatings, user);

    }

    @Override
    public List<Movie> searchMovies(List<Movie> searchBase, String query) {
        List<Movie> seachList = new ArrayList<>();
        for (Movie movie : searchBase) {
            if (movie.getTitle().toLowerCase().contains(query)) {
                seachList.add(movie);
            }
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
    public User createNewUser(String name) {
        try {
            return userDAO.createUser(name);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Could not create user");
        }
    }

    @Override
    public User getUserById(int id) {
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
