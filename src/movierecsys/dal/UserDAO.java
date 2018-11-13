/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movierecsys.dal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
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
    private static final String USER_SOURCE = "data/users.txt";

    /**
     * Gets a list of all known users.
     *
     * @return List of users.
     * @throws java.io.IOException
     */
    public List<User> getAllUsers() throws IOException
    {
        List<User> allUsers = new ArrayList<>();
        File file = new File(USER_SOURCE);

        try (BufferedReader reader = new BufferedReader(new FileReader(file)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                try
                {
                    User user = stringArrayToUsers(line);
                    allUsers.add(user);
                } catch (Exception ex)
                {
                    //Do nothing
                }
            }
        }
        return allUsers;
    }

    private User stringArrayToUsers(String str)
    {
        String[] arrUser = str.split(",");

        int id = Integer.parseInt(arrUser[0]);
        String name = arrUser[1];

        User user = new User(id, name);
        return user;
    }

    /**
     * Gets a single User by its ID.
     *
     * @param id The ID of the user.
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
     * @throws java.io.IOException
     */
    public void updateUser(User user) throws IOException
    {
        File tmp = new File(USER_SOURCE);
        List<User> allUsers = getAllUsers();
        allUsers.removeIf((User v) -> v.getId() == user.getId());
        allUsers.add(user);
        Collections.sort(allUsers, (User o1, User o2) -> Integer.compare(o1.getId(), o2.getId()));
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(tmp)))
        {
            for (User theUser : allUsers)
            {
                bw.write(theUser.getId() + "," + theUser.getName());
                bw.newLine();
            }
        }
        Files.copy(tmp.toPath(), new File(USER_SOURCE).toPath(), StandardCopyOption.REPLACE_EXISTING);

    }
}
