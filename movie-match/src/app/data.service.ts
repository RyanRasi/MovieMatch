import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable()
export class DataService {
  private data: any[] = [];
  public data$ = new BehaviorSubject<any[]>([]);
  private recommendation: any[] = [];
  public recommendation$ = new BehaviorSubject<any[]>([]);

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

  getRecommendations() {
            //      this.http.get('http://localhost:8080/api/movies+')
        //      .subscribe(
        //        data => {
        // Handle the API response data
        //          console.log(data);
        //        },
        //        error => {
        // Handle any errors
        //          console.error(error);
        //        }
        //      );
        this.recommendation.push({title: 'Cars', year: '2006', poster: 'https://image.tmdb.org/t/p/original/u4G8EkiIBZYx0wEg2xDlXZigTOZ.jpg'});
        this.recommendation$.next(this.recommendation);
  }
}