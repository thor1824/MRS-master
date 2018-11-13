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
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import movierecsys.be.Movie;
import movierecsys.be.Rating;
import movierecsys.be.User;

/**
 *
 * @author pgn
 */
public class RatingDAO {

    private static final String RATING_SOURCE = "data/ratings.txt";
    private static final String TEMP_SOURCE = "data/temp.txt";

    /**
     * Persists the given rating.
     *
     * @param rating the rating to persist.
     * @return 
     * @throws java.io.IOException
     */
    public Rating createRating(Rating rating) throws IOException {

        Path path = new File(RATING_SOURCE).toPath();
        int id = -1;
        try (BufferedWriter bw = Files.newBufferedWriter(path, StandardOpenOption.SYNC, 
                StandardOpenOption.APPEND, StandardOpenOption.WRITE))
        {
            bw.write(rating.getMovie().getId() + "," + rating.getUser().getId() + "," + rating.getRating());
        }
        return rating;
    }

    /**
     * Updates the rating to reflect the given object.
     *
     * @param newRating The updated rating to persist.
     * @throws java.io.IOException
     */
    public void updateRating(Rating newRating) throws IOException {
        File tmp = new File(RATING_SOURCE);
        List<Rating> allRating = getAllRatings();
        allRating.removeIf((Rating v) -> v.getMovie().getId() == newRating.getMovie().getId());
        allRating.add(newRating);
        Collections.sort(allRating, (Rating o1, Rating o2) -> Integer.compare(o1.getMovie().getId(), o2.getMovie().getId()));
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(tmp))) {
            for (Rating rating : allRating) {
                bw.write(rating.getMovie().getId() + "," + rating.getUser().getId() + "," + rating.getRating());
                bw.newLine();
            }
        }
        Files.copy(tmp.toPath(), new File(RATING_SOURCE).toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Removes the given rating.
     *
     * @param rating
     * @throws java.io.IOException
     */
    public void deleteRating(Rating rating) throws IOException {
        File file = new File(RATING_SOURCE);
        File temp = new File(TEMP_SOURCE);

        BufferedReader reader = new BufferedReader(new FileReader(file));
        BufferedWriter writer = new BufferedWriter(new FileWriter(temp));

        String lineToRemove;

        while ((lineToRemove = reader.readLine()) != null) {
            if (null != lineToRemove && !lineToRemove.equalsIgnoreCase(rating.toString())) {
                writer.write(lineToRemove + System.getProperty("line.separator"));
            }
        }
        writer.close();
        reader.close();

        boolean deleted = file.delete();
        boolean successful = temp.renameTo(file);
        System.out.println(successful);
    }

    /**
     * Gets all ratings from all users.
     *
     * @return List of all ratings.
     * @throws java.io.IOException
     */
    public List<Rating> getAllRatings() throws IOException {
        File file = new File(RATING_SOURCE);
        List<Rating> allRatings = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    Rating mov = createRatingFromString(line);
                    allRatings.add(mov);
                } catch (Exception ex) {
                    //Do nothing
                }
            }
        }
        return allRatings;
    }

    private Rating createRatingFromString(String strRating) throws IOException {
        UserDAO userDAO = new UserDAO();
        MovieDAO movieD = new MovieDAO();

        String[] columms = strRating.split(",");

        int userId = Integer.parseInt(columms[0]);
        User user = userDAO.getUser(userId);

        int movieId = Integer.parseInt(columms[1]);
        Movie movie = movieD.getMovie(movieId);

        int ratingOfMovie = Integer.parseInt(columms[2]);
        Rating rating = new Rating(movie, user, ratingOfMovie);
        return rating;
    }

    /**
     * Get all ratings from a specific user.
     *
     * @param user The user
     * @return The list of ratings.
     * @throws java.io.IOException
     */
    public List<Rating> getRatings(User user) throws IOException {
        List<Rating> ratingsOfUser = new ArrayList<>();
        for (Rating rating : getAllRatings()) {
            if (user.getId() == rating.getUser().getId()) {
                ratingsOfUser.add(rating);
            }
        }
        return ratingsOfUser;
    }

}
