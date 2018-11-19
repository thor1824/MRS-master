/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movierecsys.dal.DB;

import movierecsys.dal.interfaces.IMovieRepository;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.io.FileNotFoundException;
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

/**
 *
 * @author thor1
 */
public class MovieDBDAO implements IMovieRepository {

    private SQLServerDataSource ds;

    public MovieDBDAO() {
        ds = new SQLServerDataSource();
        ds.setServerName("10.176.111.31");
        ds.setDatabaseName("movie");
        ds.setUser("x");
        ds.setPassword("x");
    }

    @Override
    public Movie createMovie(int releaseYear, String title) throws IOException {
        return null;
    }

    @Override
    public void deleteMovie(Movie movie) throws FileNotFoundException, IOException {
        try (Connection con = ds.getConnection()) {
            Statement statement = con.createStatement();
            statement.executeQuery("DELETE FROM Movie WHERE ID =" + movie.getId() + ";");

        } catch (SQLServerException ex) {
            Logger.getLogger(MovieDBDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(MovieDBDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public List<Movie> getAllMovies() throws IOException {
        List<Movie> movies = new ArrayList<>();
        try (Connection con = ds.getConnection()) {
            Statement statement = con.createStatement();

            ResultSet rs = statement.executeQuery("SELECT * FROM Movie;");
            while (rs.next()) {
                int id = rs.getInt("MovieID");
                int year = rs.getInt("Year");
                String titel = rs.getString("Titel");
                Movie movie = new Movie(id, year, titel);
                movies.add(movie);
            }

        } catch (SQLException ex) {

        }
        return null;
    }

    @Override
    public Movie getMovie(int id) throws IOException {
        try (Connection con = ds.getConnection()) {
            Statement statement = con.createStatement();

            ResultSet rs = statement.executeQuery("SELECT * FROM Movie WHERE MovieID =" + id + ";");
            int year = rs.getInt("Year");
            String titel = rs.getString("Titel");
            Movie movie = new Movie(id, year, titel);
            return movie;
        } catch (SQLServerException ex) {
            Logger.getLogger(MovieDBDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(MovieDBDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void updateMovie(Movie movie) throws IOException {
        try (Connection con = ds.getConnection()) {
            Statement statement = con.createStatement();

            statement.executeQuery("UPDATE Movie SET Titel = " + movie.getTitle() + ", Year = " + movie.getYear() + " WHERE MovieID = " + movie.getId() + ";");

        } catch (SQLServerException ex) {
            Logger.getLogger(MovieDBDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(MovieDBDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
