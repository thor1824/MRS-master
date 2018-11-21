/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movierecsys.dal.DB;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import movierecsys.dal.interfaces.IUserRepository;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import movierecsys.be.User;
import movierecsys.dal.ServerConnect;

/**
 *
 * @author thor1
 */
public class UserDBDAO implements IUserRepository {

    private static ServerConnect server;

    public UserDBDAO() {
        server = new ServerConnect();
    }

    @Override
    public User createUser(String name) throws IOException {
        try (Connection con = server.getConnection()) {
            Statement statement = con.createStatement();
            int id = getNextAvailableMovieID();
            User user = new User(id, name);
            statement.execute(
                    "INSERT INTO User (UserID, Name) "
                    + "VALUES (" + id + ", '" + name + "')"
            );

            return user;

        } catch (SQLServerException ex) {
            Logger.getLogger(MovieDBDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(MovieDBDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private int getNextAvailableMovieID() throws IOException
    {
        List<User> allMovies = getAllUsers();
        int highId = allMovies.get(allMovies.size() - 1).getId();
        return highId + 1;
    }

    @Override
    public void deleteUser(User user) throws IOException {
        try (Connection con = server.getConnection()) {
            Statement statement = con.createStatement();
            statement.execute("DELETE FROM [User] "
                    + "WHERE UserID = " + user.getId()
            );

        } catch (SQLServerException ex) {
            Logger.getLogger(MovieDBDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(MovieDBDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public List<User> getAllUsers() throws IOException {
        List<User> users = new ArrayList<>();
        try (Connection con = server.getConnection()) {
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM [User]");

            while (rs.next()) {
                int id = rs.getInt("UserID");
                String name = rs.getNString("Name");
                User user = new User(id, name);
                users.add(user);
            }

        } catch (SQLException ex) {

        }
        return users;
    }

    @Override
    public User getUser(int id) throws IOException {
        try (Connection con = server.getConnection()) {
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM [User] "
                    + "WHERE UserID = " + id
            );
            rs.next();
            String name = rs.getNString("Name");
            User user = new User(id, name);

            return user;

        } catch (SQLServerException ex) {
            Logger.getLogger(MovieDBDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(MovieDBDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void updateUser(User user) throws IOException {
        try (Connection con = server.getConnection()) {
            Statement statement = con.createStatement();

            statement.execute("UPDATE [User] SET Name = '" + user.getName() + "'"
                    + " WHERE UserID = " + user.getId()
            );

        } catch (SQLServerException ex) {
            Logger.getLogger(MovieDBDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(MovieDBDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
