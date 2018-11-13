/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movierecsys.dal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import movierecsys.be.User;

/**
 *
 * @author pgn
 */
public class UserDAO
{

    /**
     * får fart i "users" i mappen af MovieRecommendationSystem-master/data
     */
    private static final String SOURCE = "data/users.txt";

    /**
     * Gets a list of all known users.
     *
     * @return List of users.
     * @throws java.io.IOException
     */
    public List<User> getAllUsers() throws IOException
    {
        List<User> allUsers = new ArrayList<>();
        String source = "data/users.txt";
        File file = new File(source);

        try (BufferedReader reader = new BufferedReader(new FileReader(file)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                try
                {
                    User mov = stringArrayToUsers(line);
                    allUsers.add(mov);
                } catch (Exception ex)
                {
                    //Do nothing
                }
            }
        }
        return allUsers;
    }

    private User stringArrayToUsers(String t)
    {
        String[] arrUser = t.split(",");

        int id = Integer.parseInt(arrUser[0]);
        String name = arrUser[1];

        User user = new User(id, name);
        return user;
    }

    /**
     * Gets a single User by its ID.
     *
     * @param id The ID of the user.
     * @param users
     * @return The User with the ID.
     * @throws java.io.IOException
     */
    public User getUser(int id) throws IOException
    {

        for (User user : getAllUsers())
        {
            if (user.getId() == id)
            {
                return user;
            }

        }
        return null;
    }

    /**
     * Updates a user so the persistence storage reflects the given User object.
     *
     * @param user The updated user.
     */
//    public void updateUser(User user)
//    {
////       File tmp = new File("data/users.txt");
////       List<User> allUsers = getAllUsers();
////       allUsers.removeIf(User v) -> v.getid() == user.getId());
//        return null;
//    }
}
