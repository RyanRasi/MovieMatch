import { Component, Input, Output, EventEmitter, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { DataService } from '../data.service';

@Component({
  selector: 'app-movie-card',
  templateUrl: './movie-card.component.html',
  styleUrls: ['./movie-card.component.sass']
})

export class MovieCardComponent implements OnInit, OnDestroy {
  selectedMovies: any[] = [];
  recommendedMovies: any[] = [];
  private dataSubscription!: Subscription;
  private recommendationSubscription!: Subscription;

  card: any[] = [];

  constructor(private dataService: DataService) { }

  ngOnInit(): void {
    this.dataSubscription = this.dataService.data$.subscribe((data) => {
      this.selectedMovies = data;
      if (this.recommendedMovies.length == 0) {
        this.card = this.selectedMovies;
      }
    });
    this.recommendationSubscription = this.dataService.recommendation$.subscribe((recommendation) => {
      this.recommendedMovies = recommendation;
      if (this.recommendedMovies.length != 0) {
        this.card = this.recommendedMovies;
      }
    });
  }

  ngOnDestroy(): void {
    this.dataSubscription.unsubscribe();
  }

  removeMovie(movie: any): void {
    this.selectedMovies = this.selectedMovies.filter(movieObj => movieObj != movie);
    this.dataService.setData(this.selectedMovies);
  }
}