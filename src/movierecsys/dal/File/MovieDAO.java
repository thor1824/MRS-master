/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movierecsys.dal.File;

import movierecsys.dal.interfaces.IMovieRepository;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
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

/**
 *
 * @author pgn
 */
public class MovieDAO implements IMovieRepository {

    private static final String MOVIE_SOURCE = "data/movie_titles.txt";
    private static final String TEMP = "data/temp.txt";

    /**
     * Gets a list of all movies in the persistence storage.
     *
     * @return List of movies.
     */
    @Override
    public List<Movie> getAllMovies() throws IOException {
        List<Movie> allMovies = new ArrayList<>();
        File file = new File(MOVIE_SOURCE);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    Movie mov = stringArrayToMovie(line);
                    allMovies.add(mov);
                } catch (Exception ex) {
                    //Do nothing
                }
            }
            reader.close();
        }
        Collections.sort(allMovies, (Movie m1, Movie m2) -> Integer.compare(m1.getId(), m2.getId()));

        return allMovies;
    }
    
    /**
     * Reads a movie from a , s
     *
     * @param t
     * @return
     * @throws NumberFormatException
     */
    private Movie stringArrayToMovie(String t) {
        String[] arrMovie = t.split(",");

        int id = Integer.parseInt(arrMovie[0]);
        int year = Integer.parseInt(arrMovie[1]);
        String title = arrMovie[2];

        Movie mov = new Movie(id, year, title);
        return mov;
    }

    /**
     * Creates a movie in the persistence storage.
     *
     * @param releaseYear The release year of the movie
     * @param title The title of the movie
     * @return The object representation of the movie added to the persistence
     * storage.
     */
    @Override
    public Movie createMovie(int releaseYear, String title) throws IOException {
        Path path = new File(MOVIE_SOURCE).toPath();
        int id = -1;
        try (BufferedWriter bw = Files.newBufferedWriter(path, StandardOpenOption.SYNC,
                StandardOpenOption.APPEND, StandardOpenOption.WRITE)) {
            id = getNextAvailableMovieID();
            bw.write(id + "," + releaseYear + "," + title);
            bw.close();
        }
        return new Movie(id, releaseYear, title);
    }

    private int getNextAvailableMovieID() throws IOException {
        List<Movie> list = getAllMovies();
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i - 1).getId() != i) {
                return i;
            }

        }
        return list.size() + 1;
    }

    /**
     * Deletes a movie from the persistence storage.
     *
     * @param movie The movie to delete.
     */
    @Override
    public void deleteMovie(Movie movie) throws FileNotFoundException, IOException {
        File file = new File(MOVIE_SOURCE);
        File temp = new File(TEMP);

        BufferedReader reader = new BufferedReader(new FileReader(file));
        BufferedWriter wrider = new BufferedWriter(new FileWriter(temp));

        String lineToRemove;

        while ((lineToRemove = reader.readLine()) != null) {
            if (null != lineToRemove && !lineToRemove.equalsIgnoreCase(movie.dataSignatur())) {
                wrider.write(lineToRemove + System.getProperty("line.separator"));

            }

        }
        wrider.close();
        reader.close();
        file.delete();
        Files.copy(temp.toPath(), file.toPath());
        temp.delete();

    }

    /**
     * Updates the movie in the persistence storage to reflect the values in the
     * given Movie object.
     *
     * @param movie The updated movie.
     * @throws java.io.IOException
     */
    @Override
    public void updateMovie(Movie movie) throws IOException {
        File tmp = new File(MOVIE_SOURCE);
        List<Movie> allMovies = getAllMovies();
        allMovies.removeIf((Movie v) -> v.getId() == movie.getId());
        allMovies.add(movie);
        Collections.sort(allMovies, (Movie o1, Movie o2) -> Integer.compare(o1.getId(), o2.getId()));
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(tmp))) {
            for (Movie theMovie : allMovies) {
                bw.write(theMovie.getId() + "," + theMovie.getYear() + "," + theMovie.getTitle());
                bw.newLine();
            }
            bw.close();
        }
        Files.copy(tmp.toPath(), new File(MOVIE_SOURCE).toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Gets a the movie with the given ID.
     *
     * @param id ID of the movie.
     * @return A Movie object.
     */
    @Override
    public Movie getMovie(int id) throws IOException {
        for (Movie movie : getAllMovies()) {
            if (movie.getId() == id) {
                return movie;
            }
        }
        return null;
    }
}
