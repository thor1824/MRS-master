/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movierecsys.gui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import movierecsys.be.Movie;
import movierecsys.be.Rating;
import movierecsys.be.User;
import movierecsys.bll.exception.MovieRecSysException;
import movierecsys.gui.model.MovieModel;

/**
 * FXML Controller class
 *
 * @author Thorbjørn Schultz Damkjær
 */
public class MovieRecViewController implements Initializable {

    @FXML
    private Label lblName;
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
    @FXML
    private Label lblTopRated;
    @FXML
    private Label lblRecomendation;
    @FXML
    private ListView<Movie> listSeach, listReccomended, listTopRated;
    @FXML
    private TextField txtUpdateMovieID, txtUpdateMovieTitel, txtUpdateMovieYear, txtSeach,
            txtDeleteMovieID, txtDeleteMovieTitel1, txtDeleteMovieYear, txtRateMovieID, txtAddMovieTitel, txtAddMovieYear, txtCreateUser;

    private MovieModel manager;
    private User loginUser;
    @FXML
    private ComboBox<User> dropUsers;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            manager = new MovieModel();
        } catch (MovieRecSysException ex) {
            Logger.getLogger(MovieRecViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

        loginUser = manager.getUserById(7);
        lblName.setText(loginUser.getName());
        dropUsers.getItems().setAll(manager.getAllUsers());

        try {
            updateUserRating();
        } catch (IOException ex) {
            Logger.getLogger(MovieRecViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        updateRecommended();
        updateHighestAvg();
    }

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
    private void rat1(ActionEvent event) throws IOException {
        manager.rateMovie(Integer.parseInt(txtRateMovieID.getText()), loginUser.getId(), -5);
        updateUserRating();
        lblRated.setText("Rated");
    }

    @FXML
    private void rat2(ActionEvent event) throws IOException {
        manager.rateMovie(Integer.parseInt(txtRateMovieID.getText()), loginUser.getId(), -3);
        updateUserRating();
        lblRated.setText("Rated");
    }

    @FXML
    private void rat3(ActionEvent event) throws IOException {
        manager.rateMovie(Integer.parseInt(txtRateMovieID.getText()), loginUser.getId(), 1);
        updateUserRating();
        lblRated.setText("Rated");
    }

    @FXML
    private void rat4(ActionEvent event) throws IOException {
        manager.rateMovie(Integer.parseInt(txtRateMovieID.getText()), loginUser.getId(), 3);
        updateUserRating();
        lblRated.setText("Rated");
    }

    @FXML
    private void rat5(ActionEvent event) throws IOException {
        manager.rateMovie(Integer.parseInt(txtRateMovieID.getText()), loginUser.getId(), 5);
        updateUserRating();
        lblRated.setText("Rated");
    }

    private void updateUserRating() throws IOException {
        listRatings.getItems().clear();
        listRatings.getItems().addAll(manager.getRatedMovies(loginUser));
        lblRated.setText("Rated");
    }

    @FXML
    private void writeSeach(KeyEvent event) {
        listSeach.getItems().clear();
        listSeach.getItems().addAll(manager.searchMovies(txtSeach.getText().toLowerCase()));
        lblRated.setText("Rated");
    }

    private void updateRecommended() {
        Thread t1 = new Thread(() -> {

            try {
                String text = lblRecomendation.getText();
                Vector<Movie> vectorRecc = new Vector<>(manager.getMovieReccomendations(loginUser));
                vectorRecc.setSize(30);
                Platform.runLater(() -> {
                    listReccomended.getItems().clear();
                    lblRecomendation.setText(text);
                    listReccomended.getItems().addAll(vectorRecc);
                });
            } catch (IOException ex) {
                Logger.getLogger(MovieRecViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        t1.setDaemon(true);
        t1.start();

    }

    private void updateHighestAvg() {
        Thread t2 = new Thread(() -> {
            try {
                String text = lblTopRated.getText();
                Vector<Movie> vectorAvg = new Vector<>(manager.getAllTimeTopRatedMovies(loginUser));
                vectorAvg.setSize(30);
                Platform.runLater(() -> {
                    
                    lblTopRated.setText(text);
                    listTopRated.getItems().addAll(vectorAvg);
                });
            } catch (IOException ex) {
                Logger.getLogger(MovieRecViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        t2.setDaemon(true);
        t2.start();
    }

    @FXML
    private void btnChangeUser(ActionEvent event) throws IOException {
        
        loginUser = dropUsers.getSelectionModel().getSelectedItem();
        lblName.setText(loginUser.getName());
        listReccomended.getItems().clear();
        updateUserRating();
        updateRecommended();
    }

}
