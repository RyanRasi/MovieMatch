import { Injectable } from '@angular/core';
import { BehaviorSubject, forkJoin, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';


@Injectable()
export class DataService {
  private data: any[] = [];
  public data$ = new BehaviorSubject<any[]>([]);
  private recommendation: any[] = [];
  public recommendation$ = new BehaviorSubject<any[]>([]);
  params = '';
  // Set a loading flag to indicate that a request is in progress
  private isLoading = false;
  public isLoading$ = new BehaviorSubject<boolean>(false);
  movie_metadata_path = 'assets/movies_metadata.csv';
  movieIds_path = 'assets/movie.csv';
  movies: any[] = [];

constructor(private http: HttpClient) { }

  readCSV(): void {
    const movieMetadata$ = this.http.get(this.movie_metadata_path, { responseType: 'text' });
    const movieIds$ = this.http.get(this.movieIds_path, { responseType: 'text' });

    forkJoin([movieMetadata$, movieIds$]).subscribe(([movieMetadata, movieIds]) => {
      this.extractColumnValues(movieMetadata, movieIds);
    });
  }

  extractColumnValues(csvData1: string, csvData2: string): void {
    const rows1 = csvData1.split('\n');
    const rows2 = csvData2.split('\n');
  
    const movieMap = new Map();

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
          this.movies.push({ title: movieTitle, year: movieReleaseDate, poster: moviePoster, movie_id: movieId });
        }
      }
    }
  }

  addData(item: any): void {
    this.data.push(item);
    this.data$.next(this.data);
  }

  setData(item: any): void {
    this.data = item;
    this.data$.next(this.data);
  }

  returnData() {
    return this.data;
  }

  resetData() {
    this.recommendation = [];
    this.recommendation$.next(this.recommendation);
    this.data = [];
    this.data$.next(this.data);
  }

  getRecommendations(requestMovies: any[]) {
    this.readCSV();
    this.params = '';
    for (let i = 0; i < requestMovies.length; i++) {
      this.params = this.params + 'movieIds=' + requestMovies[i] + '&'
    }
    // Remove last '&' character
    this.params = this.params.slice(0, -1);

    console.log('http://localhost:8080/recommendHybrid?' + this.params);
    this.isLoading$.next(true);
// Call URL
this.http.get('http://localhost:8080/recommendHybrid?' + this.params)
  .subscribe(
    (data: any) => {
      // Handle the API response data - Read the csv, save the columns, and search for the movie ID
      console.log('Recommendations from API: ' + data);
      for (let i = 0; i < data.length; i ++) {
        
        console.log('Checking for recommended movie in csv: ' + data[i]);

        let filteredMovies = this.movies.filter(movie => movie.movie_id == data[i]);
        if (filteredMovies.length != 0) {
          console.log('csv match found: ' + filteredMovies);

          this.recommendation.push(filteredMovies[0]);
          this.recommendation$.next(this.recommendation);
        } else {
          console.log('csv contains broken match')
        }
      }
      // Turn off the loading indicator when the request is complete
      this.isLoading$.next(false);
    },
    (error: any) => {
      // Handle any errors
      console.error(error);
      // Turn off the loading indicator on error as well
      this.isLoading$.next(false);
    }
  );
  }
}