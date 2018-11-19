/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movierecsys.dal;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import movierecsys.be.Movie;
import movierecsys.be.User;

/**
 *
 * @author pgn
 */
public class FileReaderTester
{

    /**
     * Example method. This is the code I used to create the users.txt files.
     *
     * @param args
     * @throws IOException
     */
    
            
    public static void main(String[] args) throws IOException
    {
        mitigateMovies();
    }
    
    public static void mitigateMovies() throws IOException
    {
        SQLServerDataSource ds = new SQLServerDataSource();
        ds.setServerName("10.176.111.31");
        ds.setDatabaseName("movie");
        ds.setUser("CS2018A_32");
        ds.setPassword("CS2018A_32");
        MovieDAO moviedao = new MovieDAO();
        List<Movie> movies = moviedao.getAllMovies();
        
        try (Connection con = ds.getConnection()) {
            Statement statement = con.createStatement();
            
            for (Movie movy : movies) {
                String sql = "INSERT INTO Movie (MovieID, Titel, Year) VALUES ("
                        + movy.getId() + ",'"
                        + movy.getTitle().replace("'", "") +"',"
                        + movy.getYear() + ");";
                int i = statement.executeUpdate(sql);
            }
            
            

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void mitigateUser() throws IOException
    {
        SQLServerDataSource ds = new SQLServerDataSource();
        ds.setServerName("10.176.111.31");
        ds.setDatabaseName("movie");
        ds.setUser("CS2018A_32");
        ds.setPassword("CS2018A_32");
        UserDAO userdao = new UserDAO();
        List<User> users = userdao.getAllUsers();
        
        try (Connection con = ds.getConnection()) {
            Statement statement = con.createStatement();
            
            for (User user : users) {
                String sql = "INSERT INTO User (UserID,Name) VALUES ("
                        + user.getId() + ","
                        + user.getName() + ");";
                int i = statement.executeUpdate(sql);
                System.out.println(sql);
            }
            

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
