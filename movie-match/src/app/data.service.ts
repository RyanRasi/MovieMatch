import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable()
export class DataService {
  private data: any[] = [];
  public data$ = new BehaviorSubject<any[]>([]);
  private recommendation: any[] = [];
  public recommendation$ = new BehaviorSubject<any[]>([]);
  http: any;
  params = '';

  constructor() { }

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
    this.params = '';
    for (let i = 0; i < requestMovies.length; i++) {
      {
        console.log(requestMovies[i]);
        this.params = this.params + 'movieIds=' + requestMovies[i] + '&'
      }
      // Remove last '&' character
      this.params = this.params.slice(0, -1);
      // Call URL
      this.http.get('http://localhost:8080/recommendHybrid?' + this.params)
        .subscribe(
          (data: any) => {
            // Handle the API response data
            console.log(data);
          },
          (error: any) => {
            // Handle any errors
            console.error(error);
          }
        );
      this.recommendation.push({ title: 'Cars', year: '2006', poster: 'https://image.tmdb.org/t/p/original/u4G8EkiIBZYx0wEg2xDlXZigTOZ.jpg' });
      this.recommendation$.next(this.recommendation);
    }
  }
}