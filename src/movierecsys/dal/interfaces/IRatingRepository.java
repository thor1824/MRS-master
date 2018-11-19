/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movierecsys.dal.interfaces;

import java.io.IOException;
import java.util.List;
import movierecsys.be.Rating;
import movierecsys.be.User;

/**
 *
 * @author thor1
 */
public interface IRatingRepository {

    /**
     * Persists the given rating.
     *
     * @param rating the rating to persist.
     * @return
     * @throws java.io.IOException
     */
    Rating createRating(Rating rating) throws IOException;

    /**
     * lav om Removes the given rating.
     *
     * @param rating
     * @throws java.io.IOException
     */
    void deleteRating(Rating rating) throws IOException;

    /**
     * peter Gets all ratings from all users.
     *
     * @return List of all ratings.
     * @throws java.io.IOException
     */
    List<Rating> getAllRatings() throws IOException;

    /**
     * peter Updates the rating to reflect the given object.
     *
     * @param newRating The updated rating to persist.
     * @throws java.io.IOException
     */
    boolean updateRating(Rating rating) throws IOException;
    
    public List<Rating> getRatings(User user);
}
