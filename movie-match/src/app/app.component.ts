import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.sass']
})
export class AppComponent {
  title = 'movie-match';
  selectedMovies: any[] = [];

  addMovie(movie: any): void {
    this.selectedMovies.push(movie);
  }

  removeMovie(movie: any): void {
    const index = this.selectedMovies.indexOf(movie);
    if (index > -1) {
      this.selectedMovies.splice(index, 1);
    }
  }
}
