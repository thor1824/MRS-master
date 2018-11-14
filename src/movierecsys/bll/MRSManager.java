/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package movierecsys.bll;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import movierecsys.be.Movie;
import movierecsys.be.Rating;
import movierecsys.be.User;
import movierecsys.bll.exception.MovieRecSysException;
import movierecsys.dal.MovieDAO;
import movierecsys.dal.RatingDAO;
import movierecsys.dal.UserDAO;

/**
 *
 * @author pgn
 */
public class MRSManager implements MRSLogicFacade
{

    private final MovieDAO movieDAO;
    private final UserDAO userDAO;
    private final RatingDAO ratingDAO;
    
    
    public MRSManager()
    {
        movieDAO = new MovieDAO();
        userDAO = new UserDAO();
        ratingDAO = new RatingDAO();
    }
    
    @Override
    public List<Rating> getRecommendedMovies(User user)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Movie> getAllTimeTopRatedMovies()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Movie> getMovieReccomendations(User user)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Movie> searchMovies(String query)
    {
        List<Movie> seachList = new ArrayList<>();  
        try {
            List<Movie> movieList = movieDAO.getAllMovies();
            for (Movie movie : movieList) {
                if (movie.getTitle().contains(query))
                    {
                        seachList.add(movie);
                    }
            }
        } catch (IOException ex) {
            throw new IllegalArgumentException("Could not delete movie");
        }
        return seachList;
    }

    @Override
    public Movie createMovie(int year, String title)
    {
        try {
           return movieDAO.createMovie(year, title);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Could not create movie");
        }
     
            
    }

    @Override
    public void updateMovie(Movie movie)
    {
        try {
            movieDAO.updateMovie(movie);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Could not update movie");
        }
    }

    @Override
    public void deleteMovie(Movie movie)
    {
        try {
            movieDAO.deleteMovie(movie);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Could not delete movie");
        } 
    }

    @Override
    public void rateMovie(Movie movie, User user, int rating)
    {
        try {
            ratingDAO.createRating(new Rating(movie, user, rating));
        } catch (IOException ex) {
            throw new IllegalArgumentException("Could not rate movie");
        }
    }

    @Override
    public User createNewUser(String name)
    {
        try {
           return userDAO.createUser(name);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Could not create user");
        }
    }

    @Override
    public User getUserById(int id)
    {
        try {
            return userDAO.getUser(id);
        } catch (IOException ex) {
            throw new IllegalArgumentException("User not found");
        }
    }

    @Override
    public List<User> getAllUsers()
    {
        try {
             return userDAO.getAllUsers();
        } catch (IOException ex) {
           throw new IllegalArgumentException("Could not get list of users");
        }
    }

    /**
     * Gets all movies.
     * @return List of movies.
     * @throws MovieRecSysException
     */
    @Override
    public List<Movie> getAllMovies() throws MovieRecSysException
    {
        try
        {
            return movieDAO.getAllMovies();
        } catch (IOException ex)
        {
//            Logger.getLogger(MRSManager.class.getName()).log(Level.SEVERE, null, ex); You could log an exception
            throw new MovieRecSysException("Could not read all movies. Cause: " + ex.getMessage());
        }
    }

}
