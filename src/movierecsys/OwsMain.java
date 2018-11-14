/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movierecsys;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.List;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import movierecsys.be.Rating;
import movierecsys.be.User;
import movierecsys.dal.MovieDAO;
import movierecsys.dal.RatingDAO;
import movierecsys.dal.UserDAO;

/**
 *
 * @author pgn
 */
public final class OwsMain extends Application
{

    @Override
    public void start(Stage stage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("/movierecsys/gui/view/MovieRecView.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line argument
     */
    public static void main(String[] args) throws IOException
    {
        MovieDAO movieDAO =  new MovieDAO();
        RatingDAO ratingDAO = new RatingDAO();
        UserDAO userDAO = new UserDAO();
        List<Rating> list = ratingDAO.getAllRatings();
        
        
        
//        Rating rating = new Rating(8,7,3);
//        System.out.println(list.get(0));
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
