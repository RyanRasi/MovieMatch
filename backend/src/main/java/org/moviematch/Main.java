package org.moviematch;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        // Press Opt+Enter with your caret at the highlighted text to see how
        // IntelliJ IDEA suggests fixing it.
        System.out.printf("Hello and welcome!\n");

        String inputJson = "[999,WrappedArray([185029,7.7595253], [177765,7.747678], [232,7.6725245], [85367,7.4693685], [3272,7.458742], [79224,7.392662], [48322,7.3919683], [1701,7.319476], [5650,7.247326], [4144,7.2335563])]";

        List<Integer> movieIds = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\[(\\d+),"); // Matches the integer within []
        Matcher matcher = pattern.matcher(inputJson);

        while (matcher.find()) {
            int movieId = Integer.parseInt(matcher.group(1));
            movieIds.add(movieId);
        }
        movieIds.remove(0);

        System.out.println(movieIds);
    }
}