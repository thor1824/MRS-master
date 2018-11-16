/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movierecsys;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import movierecsys.be.Movie;
import movierecsys.be.Rating;
import movierecsys.be.User;
import movierecsys.bll.MRSManager;
import movierecsys.dal.MovieDAO;
import movierecsys.dal.RatingDAO;
import movierecsys.dal.UserDAO;

/**
 *
 * @author pgn
 */
public final class OwsMain extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/movierecsys/gui/view/MovieRecView.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line argument
     */
    public static void main(String[] args) throws IOException {
        MovieDAO movieDAO = new MovieDAO();
        RatingDAO ratingDAO = new RatingDAO();
        UserDAO userDAO = new UserDAO();
        List<User> list = userDAO.getAllUsers();
        MRSManager manager = new MRSManager();
        List<Movie> movielist = manager.getMovieReccomendations(userDAO.getUser(7));
        Vector<Movie> movieVector = new Vector<Movie>();
        movieVector.addAll(movielist);
        movieVector.setSize(20);
        for (Movie movie : movieVector) {
            System.out.println(movie + ":      " +movie.getRecommendationValue());
        }
//        Rating rating = new Rating(8,7,3);
//        ratingDAO.updateRating(rating);
//        List<Rating> qlist = ratingDAO.getAllRatings();
//        System.out.println(qlist.get(0));
//        ratingDAO.deleteRating(rating);
//        List<Rating> alist = ratingDAO.getAllRatings();
//        System.out.println(alist.get(0));
//        
        System.out.println("done");
        System.exit(0);
    }

}
