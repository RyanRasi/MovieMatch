import { Component, OnInit } from '@angular/core';
import { DataService } from '../data.service';
import { HttpClient } from '@angular/common/http';
import { parse } from 'papaparse';
import { Subscription } from 'rxjs';

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

  pageView = "Select";

  private dataSubscription!: Subscription;

  constructor(private http: HttpClient, private dataService: DataService) { }

  ngOnInit(): void {
    this.readCSV();
  }

  readCSV(): void {
    this.http.get(this.movie_metadata_path, { responseType: 'text' })
      .subscribe(data => {
        this.extractColumnValues(data);
      });
  }

  extractColumnValues(csvData: string): void {
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
          requestMovies.push(this.selectedMovies[i].title);
        }
        console.log(requestMovies.join(","));
        this.pageView = "Recommend";
        this.dataService.getRecommendations();
      }
    } else {
      this.dataService.resetData();
      this.pageView = "Select"
    }
  }
}
