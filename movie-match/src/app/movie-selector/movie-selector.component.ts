import { Component, OnInit } from '@angular/core';
import { DataService } from '../data.service';
import { HttpClient } from '@angular/common/http';
import { parse } from 'papaparse';
import { Subscription } from 'rxjs';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-movie-selector',
  templateUrl: './movie-selector.component.html',
  styleUrls: ['./movie-selector.component.sass']
})
export class MovieSelectorComponent {
  searchInput = '';
  movieSuggestions: any[] = [];
  showDropdown = false;

  movies: any[] = [];
  selectedMovies: any[] = [];

  movie_metadata_path = 'assets/movies_metadata.csv';
  movieIds_path = 'assets/movie.csv';

  pageView = "Select";

  private dataSubscription!: Subscription;

  constructor(private http: HttpClient, private dataService: DataService) { }

  ngOnInit(): void {
    this.readCSV();
  }

  /*readCSV(): void {
    this.http.get(this.movie_metadata_path, { responseType: 'text' })
      .subscribe(movieMetadata => {
        this.extractColumnValues(movieMetadata);
      });
  };*/


  readCSV(): void {
    const movieMetadata$ = this.http.get(this.movie_metadata_path, { responseType: 'text' });
    const movieIds$ = this.http.get(this.movieIds_path, { responseType: 'text' });

    forkJoin([movieMetadata$, movieIds$]).subscribe(([movieMetadata, movieIds]) => {
      this.extractColumnValues(movieMetadata, movieIds);
    });
  }

  /*extractColumnValues(csvData: string): void {
    const rows = csvData.split('\n');
    for (let i = 1; i < rows.length; i++) {
      const columns = rows[i].split(',');
      if (columns.length > 1) {
        const movieTitle = columns[0].trim();
        const moviePoster = columns[1].trim();
        const movieReleaseDate = columns[3].trim().split("-")[0];
        this.movies.push({ title: movieTitle, year: movieReleaseDate, poster: moviePoster });
      }
    }
  }*/

  extractColumnValues(csvData1: string, csvData2: string): void {
    const rows1 = csvData1.split('\n');
    const rows2 = csvData2.split('\n');
  
    const movieMap = new Map();
    const testMap = new Map();
    // Parse the second CSV file to create a mapping of movie titles to movie IDs
    for (let i = 1; i < rows2.length; i++) {
      const columns = rows2[i].split(',');
      if (columns.length > 1) {
        const movieId = columns[0].trim().replace(/"/g, '');;
        const movieTitle = columns[1].trim().replace(/"/g, '');;
        movieMap.set(movieTitle, movieId);
      }
    }

    // Parse the first CSV file and match movie titles to movie IDs
    for (let i = 1; i < rows1.length; i++) {
      const columns = rows1[i].split(',');
      if (columns.length > 1) {
        const movieTitle = columns[0].trim();
        const moviePoster = columns[1].trim();
        const movieReleaseDate = columns[3].trim().split("-")[0];
  
        // Check if the movie title exists in the mapping
        if (movieMap.has(movieTitle + ' (' + movieReleaseDate + ')')) {
          const movieId = movieMap.get(movieTitle + ' (' + movieReleaseDate + ')');
          this.movies.push({ title: movieTitle, year: movieReleaseDate, poster: moviePoster, id: movieId });
        }
      }
    }
    console.log(this.movies)
  }

  searchMovies(): void {
    if (this.searchInput.length < 3) {
      this.showDropdown = false;
    } else {
      this.movieSuggestions = this.filterMovies(this.movies, this.searchInput);
      this.showDropdown = true;
    }
  }

  filterMovies(movies: any[], input: string): any[] {
    const filteredMovies = movies.filter((movie) =>
      movie.title.toLowerCase().includes(input.toLowerCase())
    );

    return filteredMovies.slice(0, 5);
  }

  selectMovie(movie: any): void {
    this.dataService.addData(movie);
    this.showDropdown = false; // Hide the dropdown after selecting a movie
    this.searchInput = "";
  }

  buttonTrigger() {
    if (this.pageView == "Select") {
      this.selectedMovies = this.dataService.returnData();
      if (this.selectedMovies.length <= 2) {
        console.log("Must select at least 3 movies");
      } else {
        console.log(this.selectedMovies);
        let requestMovies: any[] = []
        for (let i = 0; i < this.selectedMovies.length; i++) {
          requestMovies.push(this.selectedMovies[i].id);
        }
        console.log('Movies Selected for Recomendation: ' + requestMovies.join(","));
        this.pageView = "Recommend";
        this.dataService.getRecommendations(requestMovies);
      }
    } else {
      this.dataService.resetData();
      this.pageView = "Select"
    }
  }
}
