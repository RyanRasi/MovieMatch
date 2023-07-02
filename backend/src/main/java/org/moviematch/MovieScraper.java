package org.moviematch;

import org.apache.hadoop.shaded.com.squareup.okhttp.OkHttpClient;
import org.apache.hadoop.shaded.com.squareup.okhttp.Request;
import org.apache.hadoop.shaded.com.squareup.okhttp.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.Reader;

public class MovieScraper {

    public static final String DATASET_SIZE = "100k";
    public static final String MOVIE_IMDB_DATA_PATH = "./src/main/resources/data/" + DATASET_SIZE + "/links.csv";
    public static final String MOVIE_FALLBACK_DATA_PATH = "./src/main/resources/data/" + DATASET_SIZE + "/movies.csv";
    public static final String OUTPUT_METADATA_PATH = "./src/main/resources/data/" + DATASET_SIZE + "/movies_metadata.csv";
    public static final String BEARER_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJjYjZiNGNiZWQwYzYwY2Y1YWZmMjM0MTM0YWZkZjExNCIsInN1YiI6IjVkMjM4M2Q5NjlkMjgwMDAxMjAwNzg0YiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.qhYajRQ_ZU87_HlXVLigeCOUgUnQU3rv9IoCSKwN2u8";
    public MovieScraper() throws IOException {
    }

    public static void main(String[] args) throws IOException, JSONException {

        System.out.println("Starting Movie Metadata Scrape\n");

        List<String> imbdIDs = readMoviesCSV();

        int count = 1;

        for (String imdbID : imbdIDs) {
            System.out.println("Movie ID: tt" + imdbID);
            List<String> elements = new ArrayList<String>();

            elements.add(String.valueOf(count));
            elements.add(imdbID);

            getMovieDetails("tt" + imdbID, elements, count);

            Collections.singletonList(getMovieCredits("tt" + imdbID, elements));

            writeCSV(elements);

            count++;
        }

        System.out.println("\nMovie Metadata Scrape Has Finished Executing");

    }

    public static List<String> readMoviesCSV() {
        String filePath = MOVIE_IMDB_DATA_PATH;

        try (Reader reader = new FileReader(filePath);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            List<String> secondColumnValues = new ArrayList<>();

            for (CSVRecord csvRecord : csvParser) {
                String value = csvRecord.get(1); // Get the value from the second column (index 1)
                secondColumnValues.add(value);
            }

            return secondColumnValues;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void writeCSV (List<String> rowData) {
        String csvFilePath = OUTPUT_METADATA_PATH;

        // Append data to the CSV file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFilePath, true))) {
            // Append each field of the row
            for (int i = 0; i < rowData.size(); i++) {
                writer.append(rowData.get(i));

                // Append a comma for all fields except the last one
                if (i < rowData.size() - 1) {
                    writer.append(",");
                }
            }

            // Append a new line character
            writer.newLine();

            System.out.println("Data appended successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while appending data to the CSV file: " + e.getMessage());
        }
    }

    public static List<String> getMovieDetails(String imdbID, List<String> elements, int count) throws IOException, JSONException {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.themoviedb.org/3/movie/" + imdbID + "?language=en-US")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", BEARER_TOKEN)
                .build();

        Response response = client.newCall(request).execute();

        String responseBody = response.body().string();

        JSONObject json = new JSONObject(responseBody);

        String originalTitle = "";
        try {
            originalTitle = json.getString("original_title");
        } catch (JSONException e) {
            System.out.println("No Title Returned - Error: " + e);
            originalTitle = "N/A";
        }

        String posterPath;
        try {
            posterPath = "https://image.tmdb.org/t/p/original" + json.getString("poster_path");
        } catch (JSONException e) {
            System.out.println("No Poster Returned - Error: " + e);
            posterPath = "N/A";
        }

        String voteAverage;
        try {
            voteAverage = String.valueOf(json.getDouble("vote_average"));
        } catch (JSONException e) {
            System.out.println("No Audience Rating Returned - Error: " + e);
            voteAverage = "N/A";
        }

        String releaseDate;
        try {
            releaseDate = json.getString("release_date");
        } catch (JSONException e) {
            System.out.println("No Release Date Returned - Error: " + e);
            releaseDate = "N/A";
        }

        if (originalTitle == "NA" || releaseDate == "N/A" ) {
            TitleYear result = movieTitleAndYearFallback(count);
            originalTitle = result.getTitle();
            releaseDate = result.getYear();
        }

        elements.add(originalTitle);
        elements.add(posterPath);
        elements.add(voteAverage);
        elements.add(releaseDate);

        return elements;
    }

    final static class TitleYear {
        private final String title;
        private final String releaseDate;

        public TitleYear (String title, String releaseDate) {
            this.title = title;
            this.releaseDate = releaseDate;
        }
        public String getTitle() {
            return title;
        }
        public String getYear() {
            return releaseDate;
        }
    }
    
    public static TitleYear movieTitleAndYearFallback(int count) {
        
        String title = "N/A";
        String releaseDate = "N/A";

        String filePath = MOVIE_FALLBACK_DATA_PATH;

        try (Reader reader = new FileReader(filePath);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            for (CSVRecord csvRecord : csvParser) {
                if ((csvRecord.get(0)).equals(String.valueOf(count))) {
                    String[] titleRes = (csvRecord.get(1)).split("\\(");
                    title = titleRes[0];

                    String[] yearRes = titleRes[1].split("\\)");
                    releaseDate = yearRes[0];
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new TitleYear(title, releaseDate);
    }

    public static String getMovieCredits (String imdbID, List<String> elements) throws IOException, JSONException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.themoviedb.org/3/movie/" + imdbID + "/credits?language=en-US")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", BEARER_TOKEN)
                .build();

        Response response = client.newCall(request).execute();

        String responseBody = response.body().string();

        JSONObject json = new JSONObject(responseBody);

        String castNames = "";

        try {
            JSONArray castArray = json.getJSONArray("cast");
            for (int i = 0; i < 5; i++) {
                JSONObject castObject = castArray.getJSONObject(i);
                String name = castObject.getString("name");
                castNames = castNames + name + "|";
            }
            castNames = castNames.substring(0, castNames.length() - 1);
        } catch(JSONException e) {
            System.out.println("No Cast Returned - Error: " + e);
            castNames = "N/A";
        }

        try {
            // Get the "crew" array from the JSON
            JSONArray crewArray = json.getJSONArray("crew");
            // Iterate over the crew array and find the director
            for (int i = 0; i < crewArray.length(); i++) {
                JSONObject crewObject = crewArray.getJSONObject(i);
                String job = crewObject.getString("job");
                if (job.equals("Director")) {
                    String directorName = crewObject.getString("name");
                    elements.add(directorName);
                    break; // Assuming there is only one director
                }
            }
        } catch(JSONException e) {
            System.out.println("No Director Returned - Error: " + e);
            elements.add("N/A");
        }
        elements.add(castNames.toString());
        return elements.toString();
    }
}
