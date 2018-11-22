/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movierecsys.gui.model;

import java.io.IOException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import movierecsys.be.Movie;
import movierecsys.be.Rating;
import movierecsys.be.User;
import movierecsys.bll.MRSLogicFacade;
import movierecsys.bll.MRSManager;
import movierecsys.bll.exception.MovieRecSysException;

/**
 *
 * @author pgn
 */
public class MovieModel {

    private ObservableList<Movie> movies;
    private ObservableList<User> users;
    private MRSLogicFacade logiclayer;

    public MovieModel() throws MovieRecSysException {
        logiclayer = new MRSManager();

        movies = FXCollections.observableArrayList();
        movies.addAll(logiclayer.getAllMovies());

        users = FXCollections.observableArrayList();
        users.addAll(logiclayer.getAllUsers());
    }

    /**
     * Gets a reference to the observable list of Movies.
     *
     * @return List of movies.
     */
    public ObservableList<Movie> getMovies() {
        return movies;
    }

    public void createMovie(int year, String title) {
        Movie movie = logiclayer.createMovie(year, title);
        movies.add(movie);

    }

    public void deleteMovie(Movie movie) {
        logiclayer.deleteMovie(movie);
        movies.remove(movie);
    }

    public void updateMovie(Movie movie) {
        logiclayer.updateMovie(movie);

        for (Movie movy : movies) {
            if (movie.getId() == movy.getId()) {
                movy = movie;
            }
        }
    }

    public void rateMovie(int movieId, int userId, int rating) {
        logiclayer.rateMovie(movieId, userId, rating);
    }

    public void createNewUser(String name) {
        User user = logiclayer.createNewUser(name);
        users.add(user);
    }

    public User getUserById(int id) {
        return logiclayer.getUserById(id);
    }

    public List<Rating> getRatedMovies(User user) throws IOException {
        return logiclayer.getRatedMovies(user);
    }

    public List<Movie> getAllTimeTopRatedMovies(User user) throws IOException {
        return logiclayer.getAllTimeTopRatedMovies(user);
    }

    public List<Movie> getMovieReccomendations(User user) throws IOException {
        return logiclayer.getMovieReccomendations(user);
    }

    public List<Movie> searchMovies(String query) {
        return logiclayer.searchMovies(movies, query);
    }
    
    public List<User> getAllUsers(){
        return logiclayer.getAllUsers();
    }
}
