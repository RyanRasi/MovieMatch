package org.moviematch.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MovieController {

    @RequestMapping("/")
    public String getMovie() {
        return "Hello World!";
    }
}
