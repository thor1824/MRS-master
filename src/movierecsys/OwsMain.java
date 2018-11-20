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
import movierecsys.dal.File.MovieDAO;
import movierecsys.dal.File.RatingDAO;
import movierecsys.dal.File.UserDAO;

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
        //launch(args);
        MRSManager mrs = new MRSManager();
        UserDAO uDAO = new UserDAO();
        User user = uDAO.getUser(7);
        List<Movie> movies = mrs.getMovieReccomendations(user);
        for (Movie movy : movies) {
            System.out.println(movy);
        }
        System.exit(0);
    }

}
