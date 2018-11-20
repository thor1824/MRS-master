/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movierecsys.dal;

import movierecsys.dal.File.MovieDAO;
import movierecsys.dal.File.RatingDAO;
import movierecsys.dal.File.UserDAO;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import movierecsys.be.Movie;
import movierecsys.be.Rating;
import movierecsys.be.User;

/**
 *
 * @author pgn
 */
public class Mitigator {

    private static ServerConnect sc;

    /**
     * Example method. This is the code I used to create the users.txt files.
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        sc = new ServerConnect();
        //mitigateMovies();
        //mitigateUsers();
        //mitigateRatings();
    }

    public static void mitigateUsers() throws IOException {

        List<User> users = new UserDAO().getAllUsers();

        try (Connection con = sc.getConnection()) {
            Statement statement = con.createStatement();

            int counter = 0;
            for (User user : users) {
                String sql = "INSERT INTO [User] (UserID, Name) VALUES("
                        + user.getId() + ",'"
                        + user.getName() + "');";
                statement.addBatch(sql);
                counter++;
                if (counter % 1000 == 0) {
                    statement.executeBatch();
                    System.out.println("Added 1000 users.");
                }
            }
            if (counter % 1000 != 0) {
                statement.executeBatch();
                System.out.println("Added final batch of users.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Untested batch insert. We'll see how it goes tomorrow.
     *
     * @throws IOException
     */
    public static void mitigateRatings() throws IOException {
        List<Rating> allRatings = new RatingDAO().getAllRatings();
        
        
        try (Connection con = sc.getConnection()) {
            Statement statement = con.createStatement();

            int counter = 0;
            for (Rating rating : allRatings) {
                String sql = "INSERT INTO Rating (MovieId, UserId, Rating) VALUES ("
                        + rating.getMovie() + ","
                        + rating.getUser() + ","
                        + rating.getRating()
                        + ");";
                statement.addBatch(sql);
                counter++;
                if (counter % 1000 == 0) {
                    statement.executeBatch();
                    System.out.println("Added 1000 ratings.");
                }
            }
            if (counter % 1000 != 0) {
                statement.executeBatch();
                System.out.println("Added final batch of ratings.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void mitigateMovies() throws IOException {
        
        MovieDAO mvDao = new MovieDAO();
        List<Movie> movies = mvDao.getAllMovies();

        try (Connection con = sc.getConnection()) {
            Statement statement = con.createStatement();

            for (Movie movie : movies) {
                String sql = "INSERT INTO Movie (MovieID,Titel,Year) VALUES("
                        + movie.getId() + ",'"
                        + movie.getTitle().replace("'", "") + "',"
                        + movie.getYear() + ");";
                System.out.println(sql);
                int i = statement.executeUpdate(sql);
                // INSERT INTO Movie (id,year,title) VALUES (1,2018,Venom);
                System.out.println("Affected row = " + i);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void createRafFriendlyRatingsFile() throws IOException {
        String target = "data/user_ratings";
        RatingDAO ratingDao = new RatingDAO();
        List<Rating> all = ratingDao.getAllRatings();

        try (RandomAccessFile raf = new RandomAccessFile(target, "rw")) {
            for (Rating rating : all) {
                raf.writeInt(rating.getMovie());
                raf.writeInt(rating.getUser());
                raf.writeInt(rating.getRating());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
