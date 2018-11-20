/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movierecsys.gui.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import movierecsys.be.Movie;
import movierecsys.be.Rating;
import movierecsys.be.User;
import movierecsys.bll.MRSManager;

/**
 * FXML Controller class
 *
 * @author Shark
 */
public class MovieRecViewController implements Initializable {

    @FXML
    private Label lblName;
    @FXML
    private ListView<Movie> listSeach, listReccomended, listTopRated;
    @FXML
    private TextField txtUpdateMovieID, txtUpdateMovieTitel, txtUpdateMovieYear, txtSeach,
            txtDeleteMovieID, txtDeleteMovieTitel1, txtDeleteMovieYear, txtRateMovieID, txtAddMovieTitel, txtAddMovieYear, txtCreateUser;
    MRSManager manager;
    User loginUser;
    @FXML
    private Label lblAdd;
    @FXML
    private Label lblUpdated;
    @FXML
    private Label lblDeleted;
    @FXML
    private Label lblCreated;
    @FXML
    private Label lblRated;
    @FXML
    private ListView<Rating> listRatings;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        boolean fullLoad = false;
        manager = new MRSManager();
        System.out.println("20%");

        loginUser = manager.getUserById(7);
        System.out.println("40%");

        lblName.setText(loginUser.getName());
        updateUserRating();
        System.out.println("60%");

        if (fullLoad) {
            try {
                Vector<Movie> vectorRecc = new Vector<>(manager.getMovieReccomendations(loginUser));
                vectorRecc.setSize(30);
                listReccomended.getItems().addAll(vectorRecc);
                System.out.println("80%");
                
                Vector<Movie> vectorAvg = new Vector<>(manager.getAllTimeTopRatedMovies());
                vectorAvg.setSize(30);
                listTopRated.getItems().addAll(vectorAvg);
                System.out.println("100%");
            } catch (IOException ex) {
                Logger.getLogger(MovieRecViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void btnSeach(ActionEvent event) {
        listSeach.getItems().clear();
        listSeach.getItems().addAll(manager.searchMovies(txtSeach.getText().toLowerCase()));

    }

    @FXML
    private void btnAddMovie(ActionEvent event) {
        lblAdd.setText("");
        manager.createMovie(Integer.parseInt(txtAddMovieYear.getText()), txtAddMovieTitel.getText());
        lblAdd.setText("Movie Added");
        txtAddMovieTitel.clear();
        txtAddMovieTitel.clear();
    }

    @FXML
    private void btnUpdateMovie(ActionEvent event) {
        lblUpdated.setText("");
        Movie movie = new Movie(Integer.parseInt(txtUpdateMovieID.getText()), Integer.parseInt(txtUpdateMovieYear.getText()), txtUpdateMovieTitel.getText());
        manager.updateMovie(movie);
        lblUpdated.setText("Movie Updated");
        txtUpdateMovieID.clear();
        txtUpdateMovieTitel.clear();
        txtUpdateMovieYear.clear();
    }

    @FXML
    private void btnDeleteUser(ActionEvent event) {
        lblDeleted.setText("");
        manager.deleteMovie(new Movie(Integer.parseInt(txtDeleteMovieID.getText()),
                Integer.parseInt(txtDeleteMovieYear.getText()),
                txtDeleteMovieTitel1.getText()));
        lblDeleted.setText("Movie was Deleted");
        txtDeleteMovieID.clear();
        txtDeleteMovieTitel1.clear();
        txtDeleteMovieYear.clear();
    }

    @FXML
    private void btnCreateUser(ActionEvent event) {
        lblCreated.setText("");
        manager.createNewUser(txtCreateUser.getText());
        lblCreated.setText("User Created");
        txtCreateUser.clear();
    }

    @FXML
    private void rat1(ActionEvent event) {
        manager.rateMovie(Integer.parseInt(txtRateMovieID.getText()), loginUser.getId(), -5);
        updateUserRating();
    }

    @FXML
    private void rat2(ActionEvent event) {
        manager.rateMovie(Integer.parseInt(txtRateMovieID.getText()), loginUser.getId(), -3);
        updateUserRating();
    }

    @FXML
    private void rat3(ActionEvent event) {
        manager.rateMovie(Integer.parseInt(txtRateMovieID.getText()), loginUser.getId(), 1);
        updateUserRating();
    }

    @FXML
    private void rat4(ActionEvent event) {
        manager.rateMovie(Integer.parseInt(txtRateMovieID.getText()), loginUser.getId(), 3);
        updateUserRating();
    }

    @FXML
    private void rat5(ActionEvent event) {
        manager.rateMovie(Integer.parseInt(txtRateMovieID.getText()), loginUser.getId(), 5);
        updateUserRating();
    }

    private void updateUserRating() {
        listRatings.getItems().clear();
        listRatings.getItems().addAll(manager.getRatedMovies(loginUser));

    }

    @FXML
    private void writeSeach(KeyEvent event) {
        listSeach.getItems().clear();
        listSeach.getItems().addAll(manager.searchMovies(txtSeach.getText().toLowerCase()));
    }

}
