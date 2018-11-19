/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movierecsys.dal.File;

import movierecsys.dal.interfaces.IUserRepository;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import movierecsys.be.User;

/**
 *
 * @author pgn
 */
public class UserDAO implements IUserRepository {

    /**
     * får fart i "users" i mappen af MovieRecommendationSystem-master/data
     */
    private static final String USER_SOURCE = "data/users.txt";
    private static final String TEMP_SOURCE = "data/temp.txt";

    /**
     * Gets a list of all known users.
     *
     * @return List of users.
     * @throws java.io.IOException
     */
    @Override
    public List<User> getAllUsers() throws IOException {
        List<User> allUsers = new ArrayList<>();
        File file = new File(USER_SOURCE);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    User user = stringArrayToUsers(line);
                    allUsers.add(user);
                } catch (Exception ex) {
                    //Do nothing
                }
            }
        }
        return allUsers;
    }

    /**
     * Creates a new user form a String 
     * format for string should be: "ID,Name"
     * 
     * @param str
     * @return 
     */
    private User stringArrayToUsers(String str) {
        String[] arrUser = str.split(",");

        int id = Integer.parseInt(arrUser[0]);
        String name = arrUser[1];

        User user = new User(id, name);
        return user;
    }

    /**
     * Creates a new user and adds it to the data file
     * 
     * @param name
     * @return
     * @throws IOException 
     */
    @Override
    public User createUser(String name) throws IOException {
        Path path = new File(USER_SOURCE).toPath();
        int id = -1;
        try (BufferedWriter bw = Files.newBufferedWriter(path, StandardOpenOption.SYNC,
                StandardOpenOption.APPEND, StandardOpenOption.WRITE)) {
            id = getNextAvailableUserID();
            bw.write(id + "," + name);
        }
        return new User(id, name);
    }

    /**
     * genrates the first free available user ID
     * 
     * @return
     * @throws IOException 
     */
    private int getNextAvailableUserID() throws IOException {
        Path path = new File(USER_SOURCE).toPath();
        Stream<String> stream = Files.lines(path, Charset.defaultCharset());
        String highIdLine = stream.max(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int id1, id2;
                String[] arrOne = o1.split(",");
                String[] arrTwo = o2.split(",");
                try {
                    id1 = Integer.parseInt(arrOne[0]);
                } catch (NumberFormatException nfe) {
                    id1 = -1;
                }
                try {
                    id2 = Integer.parseInt(arrTwo[0]);;
                } catch (NumberFormatException mfe) {
                    id2 = -1;
                }
                return Integer.compare(id1, id2);
            }
        }).get();
        return Integer.parseInt(highIdLine.split(",")[0]) + 1;
    }

    /**
     * Gets a single User by its ID.
     *
     * @param id The ID of the user.
     * @return The User with the ID.
     * @throws java.io.IOException
     */
    @Override
    public User getUser(int id) throws IOException {

        for (User user : getAllUsers()) {
            if (user.getId() == id) {
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
    @Override
    public void updateUser(User user) throws IOException {
        File tmp = new File(USER_SOURCE);
        List<User> allUsers = getAllUsers();
        allUsers.removeIf((User v) -> v.getId() == user.getId());
        allUsers.add(user);
        Collections.sort(allUsers, (User o1, User o2) -> Integer.compare(o1.getId(), o2.getId()));
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(tmp))) {
            for (User theUser : allUsers) {
                bw.write(theUser.getId() + "," + theUser.getName());
                bw.newLine();
            }
        }
        Files.copy(tmp.toPath(), new File(USER_SOURCE).toPath(), StandardCopyOption.REPLACE_EXISTING);

    }

    /**
     * Deletes a user from the data file
     * 
     * @param user
     * @throws IOException 
     */
    @Override
    public void deleteUser(User user) throws IOException {
        File file = new File(USER_SOURCE);
        File temp = new File(TEMP_SOURCE);

        BufferedReader reader = new BufferedReader(new FileReader(file));
        BufferedWriter writer = new BufferedWriter(new FileWriter(temp));

        String lineToRemove;

        while ((lineToRemove = reader.readLine()) != null) {
            if (null != lineToRemove && !lineToRemove.equalsIgnoreCase(user.toString())) {
                writer.write(lineToRemove + System.getProperty("line.separator"));
            }
        }
        writer.close();
        reader.close();

        boolean deleted = file.delete();
        boolean successful = temp.renameTo(file);
        System.out.println(successful);
    }
}
