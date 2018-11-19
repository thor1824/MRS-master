/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movierecsys.dal.interfaces;

import java.io.IOException;
import java.util.List;
import movierecsys.be.User;

/**
 *
 * @author thor1
 */
public interface IUserRepository {

    /**
     * Creates a new user and adds it to the data file
     *
     * @param name
     * @return
     * @throws IOException
     */
    User createUser(String name) throws IOException;

    /**
     * Deletes a user from the data file
     *
     * @param user
     * @throws IOException
     */
    void deleteUser(User user) throws IOException;

    /**
     * Gets a list of all known users.
     *
     * @return List of users.
     * @throws java.io.IOException
     */
    List<User> getAllUsers() throws IOException;

    /**
     * Gets a single User by its ID.
     *
     * @param id The ID of the user.
     * @return The User with the ID.
     * @throws java.io.IOException
     */
    User getUser(int id) throws IOException;

    /**
     * Updates a user so the persistence storage reflects the given User object.
     *
     * @param user The updated user.
     * @throws java.io.IOException
     */
    void updateUser(User user) throws IOException;
    
}
