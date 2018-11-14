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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import movierecsys.be.Movie;

/**
 *
 * @author pgn
 */
public class MovieDAO {

    private static final String MOVIE_SOURCE = "data/movie_titles.txt";

    /**
     * Gets a list of all movies in the persistence storage.
     *
     * @return List of movies.
     */
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
        }
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
    public Movie createMovie(int releaseYear, String title) throws IOException {
        Path path = new File(MOVIE_SOURCE).toPath();
        int id = -1;
        try (BufferedWriter bw = Files.newBufferedWriter(path, StandardOpenOption.SYNC,
                StandardOpenOption.APPEND, StandardOpenOption.WRITE)) {
            id = getNextAvailableMovieID();
            bw.write(id + "," + releaseYear + "," + title);
        }
        return new Movie(id, releaseYear, title);
    }

    private int getNextAvailableMovieID() throws IOException {
        Path path = new File(MOVIE_SOURCE).toPath();
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
     * Deletes a movie from the persistence storage.
     *
     * @param movie The movie to delete.
     */
    public void deleteMovie(Movie movie) throws FileNotFoundException, IOException {
        File file = new File(MOVIE_SOURCE);
        File midlertidig = new File("E:\\GitHub\\MRS-master\\data\\temp.txt");

        BufferedReader reader = new BufferedReader(new FileReader(file));
        BufferedWriter wrider = new BufferedWriter(new FileWriter(midlertidig));

        String lineToRemove;

        while ((lineToRemove = reader.readLine()) != null) {
            if (null != lineToRemove && !lineToRemove.equalsIgnoreCase(movie.toString())) {
                wrider.write(lineToRemove + System.getProperty("line.separator"));

            }

        }
        wrider.close();
        reader.close();

        boolean deleted = file.delete();
        boolean successful = midlertidig.renameTo(file);
        System.out.println(successful);

    }

    /**
     * Updates the movie in the persistence storage to reflect the values in the
     * given Movie object.
     *
     * @param movie The updated movie.
     * @throws java.io.IOException
     */
    public void updateMovie(Movie movie) throws IOException {
        File tmp = new File(MOVIE_SOURCE);
        List<Movie> allMovies = getAllMovies();
        allMovies.removeIf((Movie v) -> v.getId() == movie.getId());
        allMovies.add(movie);
        Collections.sort(allMovies, (Movie o1,Movie o2)-> Integer.compare(o1.getId(), o2.getId()));
         try (BufferedWriter bw = new BufferedWriter(new FileWriter(tmp)))
         {
             for (Movie theMovie : allMovies)
             {
                 bw.write(theMovie.getId() + "," + theMovie.getYear() + "," + theMovie.getTitle());
                 bw.newLine();
             }
         }
         Files.copy(tmp.toPath(), new File(MOVIE_SOURCE).toPath(),StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Gets a the movie with the given ID.
     *
     * @param id ID of the movie.
     * @return A Movie object.
     */
    public Movie getMovie(int id) throws IOException {
        for (Movie movie : getAllMovies()) {
            if (movie.getId() == id) {
                return movie;
            }
        }
        return null;
    }
}
