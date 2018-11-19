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

/**
 *
 * @author pgn
 */
public class MRSManager implements MRSLogicFacade {

    private final MovieDAO movieDAO;
    private final UserDAO userDAO;
    private final RatingDAO ratingDAO;

    public MRSManager() {

        movieDAO = new MovieDAO();
        ratingDAO = new RatingDAO();
        userDAO = new UserDAO();

    }

    @Override
    public List<Rating> getRatedMovies(User user) {
        try {
            return ratingDAO.getRatings(user, ratingDAO.getAllRatings());
        } catch (IOException ex) {
            throw new IllegalArgumentException("Something went wrong when getting ratings from " + user.getName());
        }
    }

    @Override
    public List<Movie> getAllTimeTopRatedMovies() {
        int size = 0;
        List<Movie> movies = new ArrayList();
        try {
            movies.addAll(movieDAO.getAllMovies());
        } catch (IOException ex) {
            System.out.println("cheese");
        }

        List<Rating> ratings = new ArrayList<>();
        try {
            ratings.addAll(ratingDAO.getAllRatings());
        } catch (IOException ex) {
            System.out.println("cheese");
        }

        for (Rating rating : ratings) {
            for (Movie movie : movies) {
                if (rating.getMovie() == movie.getId()) {
                    movie.addToRatings(rating.getRating());
                    break;
                }
            }

        }

        Collections.sort(movies, (Movie m1, Movie m2) -> Double.compare(m2.getAvgRating(), m1.getAvgRating()));
        return movies;
    }

    @Override
    public List<Movie> getMovieReccomendations(User user) {
        try {
            List<Rating> ratinglist = ratingDAO.getAllRatings();
            List<Rating> userRatings = ratingDAO.getRatings(userDAO.getUser(user.getId()), ratinglist);
            List<Movie> movies = movieDAO.getAllMovies();
            List<User> pairedUsers = userDAO.getAllUsers();
            int listSize = 0;

            for (User pairedUser : pairedUsers) {
                List<Rating> listOfRatings = ratingDAO.getRatings(pairedUser, ratinglist);
                int similarity = 0;
                if (listOfRatings.size() > 3) {

                    for (Rating pairedUserRating : listOfRatings) {
                        for (Rating userRating : userRatings) {
                            if (userRating.getMovie() == pairedUserRating.getMovie()) {
                                similarity += userRating.getRating() * pairedUserRating.getRating();
                                break;
                            }
                        }
                    }
                    for (Rating PairedUserRating : listOfRatings) {
                        for (Movie movie : movies) {
                            if (PairedUserRating.getMovie() == movie.getId()) {
                                movie.setRecommendationValue(movie.getRecommendationValue() + similarity * PairedUserRating.getRating());

                            }
                        }
                    }
                }
                listSize++;
                if (listSize == 1000) {
                    break;
                }
            }

            Collections.sort(movies, (Movie m1, Movie m2) -> Double.compare(m2.getRecommendationValue(), m1.getRecommendationValue()));
            return movies;
        } catch (IOException ex) {
            throw new IllegalArgumentException("Could not get Recommended list");
        }
    }

    public List<Movie> getMovieReccomendations1(User user) throws IOException {
        List<Rating> ratinglist = ratingDAO.getAllRatings();
        List<Rating> userRatings = ratingDAO.getRatings(user, ratinglist);

        List<Rating> pairedUserRatings = new ArrayList<>();
        List<Movie> movies = movieDAO.getAllMovies();
        List<Movie> reccommendedMovies = new ArrayList<>();
        List<User> pairedUsers = userDAO.getAllUsers();
        pairedUsers.remove(user);

        Collections.sort(ratinglist, (Rating r1, Rating r2) -> Double.compare(r2.getUser(), r1.getUser()));

        Rating priviuseRating = new Rating(0, 0, 0);
        for (Rating rating : ratinglist) {

            if (rating.getUser() != priviuseRating.getUser()) {

                List<Rating> pairedTemp = new ArrayList();
                List<Rating> userTempRatings = new ArrayList();
                for (Rating userRating : userRatings) {
                    for (Rating pairedUserRating : pairedUserRatings) {
                        if (userRating.getMovie() == pairedUserRating.getMovie()) {
                            pairedTemp.add(pairedUserRating);
                            userTempRatings.add(userRating);
                            break;
                        }
                    }
                }
                if (!pairedTemp.isEmpty()) {

                    User pairedUser = userDAO.getUser(priviuseRating.getUser());

                    List<Movie> ratedMovies = movieDAO.movieListfromRatingsIDs(pairedUserRatings, movies);

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
                    pairedUser.setSimilarity(similarity);
                    reccommendedMovies.removeAll(ratedMovies);

                    i = 0;
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

                pairedUserRatings = new ArrayList<>();
            } else {
                pairedUserRatings.add(rating);
            }
            priviuseRating = rating;
        }
        Collections.sort(reccommendedMovies, (Movie m1, Movie m2) -> Integer.compare(m2.getRecommendationValue(), m1.getRecommendationValue())) ;
        System.out.println("done");
        return reccommendedMovies;
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
