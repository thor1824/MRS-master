/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movierecsys.dal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
    private static final int DELETEDVALUE = -1;

    /**
     * Persists the given rating.
     *
     * @param rating the rating to persist.
     * @return
     * @throws java.io.IOException
     */
    public Rating createRating(Rating rating) throws IOException {

        FileOutputStream output = new FileOutputStream(RATING_SOURCE, true);
        try {
            output.write(rating.getMovie());
            output.write(rating.getUser());
            output.write(rating.getRating());

        } finally {
            output.close();
        }
        return rating;

    }

    /**
     * peter Updates the rating to reflect the given object.
     *
     * @param newRating The updated rating to persist.
     * @throws java.io.IOException
     */
    public void updateRating(Rating rating) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(RATING_SOURCE, "rw")) {
            long totalRatings = raf.length();
            long low = 0;
            long high = ((totalRatings - 1) / RECORD_SIZE) * RECORD_SIZE;
            while (high >= low) //Binary search of movie ID
            {
                long pos = (((high + low) / 2) / RECORD_SIZE) * RECORD_SIZE;
                raf.seek(pos);
                int movId = raf.readInt();
                int userId = raf.readInt();

                if (rating.getMovie() < movId) //We did not find the movie.
                {
                    high = pos - RECORD_SIZE; //We half our problem size to the upper half.
                } else if (rating.getMovie() > movId) //We did not find the movie.
                {
                    low = pos + RECORD_SIZE; //We half our problem size (Just the lower half)
                } else //We found a movie match, not to search for the user:
                {
                    if (rating.getUser() < userId) //Again we half our problem size
                    {
                        high = pos - RECORD_SIZE;
                    } else if (rating.getUser() > userId) //Another half sized problem
                    {
                        low = pos + RECORD_SIZE;
                    } else //Last option, we found the right row:
                    {
                        raf.writeInt(rating.getRating()); //Remember the to reads at line 60,61. They positioned the filepointer just at the ratings part of the current record.
                        return; //We return from the method. We are done here. The try with resources will close the connection to the file.
                    }
                }
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("Rating not found in file");
        }

    }

    /**
     * lav om Removes the given rating.
     *
     * @param rating
     * @throws java.io.IOException
     */
    public void deleteRating(Rating rating) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(RATING_SOURCE, "rw")) {
            long totalRatings = raf.length();

            long deleteAtPos = 0;
            long low = 0;
            long high = ((totalRatings - 1) / RECORD_SIZE) * RECORD_SIZE;
            while (high >= low) //Binary search of movie ID
            {
                long pos = (((high + low) / 2) / RECORD_SIZE) * RECORD_SIZE;
                raf.seek(pos);
                int movId = raf.readInt();
                int userId = raf.readInt();

                if (rating.getMovie() < movId) //We did not find the movie.
                {
                    high = pos - RECORD_SIZE; //We half our problem size to the upper half.
                } else if (rating.getMovie() > movId) //We did not find the movie.
                {
                    low = pos + RECORD_SIZE; //We half our problem size (Just the lower half)
                } else //We found a movie match, not to search for the user:
                {
                    if (rating.getUser() < userId) //Again we half our problem size
                    {
                        high = pos - RECORD_SIZE;
                    } else if (rating.getUser() > userId) //Another half sized problem
                    {
                        low = pos + RECORD_SIZE;
                    } else //Last option, we found the right row:
                    {
                        deleteAtPos = raf.getFilePointer() - Integer.BYTES * 2;
                        raf.seek(deleteAtPos);
                        raf.writeInt(DELETEDVALUE);
                        raf.writeInt(DELETEDVALUE);
                        raf.writeInt(DELETEDVALUE);
                    }
                }
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("Rating not found in file");
        }

    }

    /**
     * peter Gets all ratings from all users.
     *
     * @return List of all ratings.
     * @throws java.io.IOException
     */
    public List<Rating> getAllRatings() throws IOException {
        List<Rating> allRatings = new ArrayList<>();
        MovieDAO moviedao = new MovieDAO();
        UserDAO userdao = new UserDAO();
        byte[] all = Files.readAllBytes(new File(RATING_SOURCE).toPath()); //I get all records as binary data!
        for (int i = 0; i < all.length; i += RECORD_SIZE) {
            int movieId = ByteBuffer.wrap(all, i, Integer.BYTES).order(ByteOrder.BIG_ENDIAN).getInt();
            int userId = ByteBuffer.wrap(all, i + Integer.BYTES, Integer.BYTES).order(ByteOrder.BIG_ENDIAN).getInt();
            int rating = ByteBuffer.wrap(all, i + Integer.BYTES * 2, Integer.BYTES).order(ByteOrder.BIG_ENDIAN).getInt();
            if (movieId != DELETEDVALUE) {
                Rating r = new Rating(movieId, userId, rating);
                allRatings.add(r);
            } else {
                System.out.println("found deleted file");
            }

        }
        System.out.println("finish reading");
        Collections.sort(allRatings, (Rating o1, Rating o2)
                -> {
            int movieCompare = Integer.compare(o1.getMovie(), o2.getMovie());
            return movieCompare == 0 ? Integer.compare(o1.getUser(), o2.getUser()) : movieCompare;
        });
        System.out.println("finnished sorting");
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
            if (user.getId() == rating.getUser()) {
                ratingsOfUser.add(rating);
            }
        }
        return ratingsOfUser;
    }

}
