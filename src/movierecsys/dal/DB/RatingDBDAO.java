/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movierecsys.dal.DB;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import movierecsys.dal.interfaces.IRatingRepository;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import movierecsys.be.Movie;
import movierecsys.be.Rating;
import movierecsys.be.User;

/**
 *
 * @author thor1
 */
public class RatingDBDAO implements IRatingRepository {

    private static ServerConnect server;

    public RatingDBDAO() {
        server = new ServerConnect();
    }

    @Override
    public Rating createRating(Rating rating) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteRating(Rating rating) throws IOException {
        try (Connection con = server.getConnection()) {
            Statement statement = con.createStatement();
            statement.executeQuery("DELETE FROM Rating WHERE MovieID =" + rating.getMovie() + ", UserID" + rating.getUser());

        } catch (SQLServerException ex) {
            Logger.getLogger(MovieDBDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(MovieDBDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public List<Rating> getAllRatings() throws IOException {
        List<Rating> ratingList = new ArrayList<>();
        try (Connection con = server.getConnection()) {
            Statement statement = con.createStatement();

            ResultSet rs = statement.executeQuery("SELECT * FROM Rating");
            while (rs.next()) {
                int movieID = rs.getInt("MovieID");
                int userID = rs.getInt("UserID");
                int score = rs.getInt("Rating");
                Rating rating = new Rating(movieID, userID, score);
                ratingList.add(rating);
            }

        } catch (SQLException ex) {

        }
        return ratingList;
    }

    @Override
    public boolean updateRating(Rating rating) throws IOException {
        try (Connection con = server.getConnection()) {
            Statement statement = con.createStatement();

            statement.executeQuery("UPDATE Movie SET Rating = " + rating.getRating() + " WHERE MovieID = " + rating.getMovie() + ", UserID =" + rating.getUser());
            return true;
        } catch (SQLServerException ex) {
            Logger.getLogger(MovieDBDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(MovieDBDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public List<Rating> getRatings(User user) {
        List<Rating> userRatings = new ArrayList<>();
        
        try (Connection con = server.getConnection()) {
            Statement statement = con.createStatement();

            ResultSet rs = statement.executeQuery("SELECT * FROM Rating WHERE UserID = " + user.getId());
            while (rs.next() && rs.getInt("UserID") == user.getId()) {
                int movieID = rs.getInt("MovieID");
                int userID = rs.getInt("UserID");
                int score = rs.getInt("Rating");
                Rating rating = new Rating(movieID, userID, score);
                userRatings.add(rating);
            }
        } catch (SQLServerException ex) {
            Logger.getLogger(MovieDBDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(MovieDBDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return userRatings;
    }
}
