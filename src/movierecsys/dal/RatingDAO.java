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
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import movierecsys.be.Rating;
import movierecsys.be.User;

/**
 *
 * @author pgn
 */
public class RatingDAO {
    private static final String TEMP_SOURCE = "data/temp.txt";
    private static final String RATING_SOURCE = "data/user_ratings";
    private static final int RECORD_SIZE = Integer.BYTES * 3;

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

    /**peter
     * Updates the rating to reflect the given object.
     *
     * @param newRating The updated rating to persist.
     * @throws java.io.IOException
     */
    public void updateRating(Rating rating) throws IOException
    {
        try (RandomAccessFile raf = new RandomAccessFile(RATING_SOURCE, "rw"))
        {
            long totalRatings = raf.length();
            long low = 0;
            long high = ((totalRatings - 1) / RECORD_SIZE) * RECORD_SIZE;
            while (high >= low) //Binary search of movie ID
            {
                long pos = (((high + low) / 2) / RECORD_SIZE) * RECORD_SIZE;
                raf.seek(pos);
                int movId = raf.readInt();
                int userId = raf.readInt();

                if (rating.getMovie().getId() < movId) //We did not find the movie.
                {
                    high = pos - RECORD_SIZE; //We half our problem size to the upper half.
                } else if (rating.getMovie().getId() > movId) //We did not find the movie.
                {
                    low = pos + RECORD_SIZE; //We half our problem size (Just the lower half)
                } else //We found a movie match, not to search for the user:
                {
                    if (rating.getUser().getId() < userId) //Again we half our problem size
                    {
                        high = pos - RECORD_SIZE;
                    } else if (rating.getUser().getId() > userId) //Another half sized problem
                    {
                        low = pos + RECORD_SIZE;
                    } else //Last option, we found the right row:
                    {
                        raf.write(rating.getRating()); //Remember the to reads at line 60,61. They positioned the filepointer just at the ratings part of the current record.
                        return; //We return from the method. We are done here. The try with resources will close the connection to the file.
                    }
                }
            }
        }
        throw new IllegalArgumentException("Rating not found in file, can't update!"); //If we reach this point we have been searching for a non-present rating.
    }

    /** lav om
     * Removes the given rating.
     *
     * @param rating
     * @throws java.io.IOException
     */
    public void deleteRating(Rating rating) throws IOException {
        File file = new File(RATING_SOURCE);
        File temp = new File(TEMP_SOURCE);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(temp));
            
            String lineToRemove;
            
            while ((lineToRemove = reader.readLine()) != null) {
                if (null != lineToRemove && !lineToRemove.equalsIgnoreCase(rating.toString())) {
                    writer.write(lineToRemove + System.getProperty("line.separator"));
                }
            }
            writer.close();
        }

        file.delete();
        temp.renameTo(file);
        
    }

    /** peter
     * Gets all ratings from all users.
     *
     * @return List of all ratings.
     * @throws java.io.IOException
     */
    public List<Rating> getAllRatings() throws IOException
    {
        List<Rating> allRatings = new ArrayList<>();
        MovieDAO moviedao = new MovieDAO();
        UserDAO userdao = new UserDAO();
        byte[] all = Files.readAllBytes(new File(RATING_SOURCE).toPath()); //I get all records as binary data!
        for (int i = 0; i < all.length; i += RECORD_SIZE)
        {
            int movieId = ByteBuffer.wrap(all, i, Integer.BYTES).order(ByteOrder.BIG_ENDIAN).getInt();
            int userId = ByteBuffer.wrap(all, i + Integer.BYTES, Integer.BYTES).order(ByteOrder.BIG_ENDIAN).getInt();
            int rating = ByteBuffer.wrap(all, i + Integer.BYTES * 2, Integer.BYTES).order(ByteOrder.BIG_ENDIAN).getInt();
            Rating r = new Rating(moviedao.getMovie(movieId), userdao.getUser(userId), rating);
            allRatings.add(r);
        }
        Collections.sort(allRatings, (Rating o1, Rating o2) ->
        {
            int movieCompare = Integer.compare(o1.getMovie().getId(), o2.getMovie().getId());
            return movieCompare == 0 ? Integer.compare(o1.getUser().getId(), o2.getUser().getId()) : movieCompare;
        });
        return allRatings;
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
