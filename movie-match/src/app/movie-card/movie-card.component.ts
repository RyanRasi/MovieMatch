import { Component, Input, Output, EventEmitter, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { DataService } from '../data.service';

@Component({
  selector: 'app-movie-card',
  templateUrl: './movie-card.component.html',
  styleUrls: ['./movie-card.component.sass']
})
export class MovieCardComponent implements OnInit, OnDestroy {
  movies: any[] = [];
  private dataSubscription!: Subscription;

  constructor(private dataService: DataService) { }

  ngOnInit(): void {
    this.dataSubscription = this.dataService.data$.subscribe((data) => {
      this.movies = data;
    });
  }

  ngOnDestroy(): void {
    this.dataSubscription.unsubscribe();
  }

  removeMovie(movie: any): void {
    this.movies = this.movies.filter(movieObj => movieObj != movie);
    this.dataService.setData(this.movies);
  }
}