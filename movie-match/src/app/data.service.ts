import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';


@Injectable()
export class DataService {
  private data: any[] = [];
  public data$ = new BehaviorSubject<any[]>([]);
  private recommendation: any[] = [];
  public recommendation$ = new BehaviorSubject<any[]>([]);
  params = '';
  // Set a loading flag to indicate that a request is in progress
isLoading = true;

constructor(private http: HttpClient) { }


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
      this.params = this.params + 'movieIds=' + requestMovies[i] + '&'
    }
    // Remove last '&' character
    this.params = this.params.slice(0, -1);

    console.log('http://localhost:8080/recommendHybrid?' + this.params);

// Call URL
this.http.get('http://localhost:8080/recommendHybrid?' + this.params)
  .subscribe(
    (data: any) => {
      // Handle the API response data - Read the csv, save the columns, and search for the movie ID
      console.log(data);
      // Turn off the loading indicator when the request is complete
      this.isLoading = false;
    },
    (error: any) => {
      // Handle any errors
      console.error(error);
      // Turn off the loading indicator on error as well
      this.isLoading = false;
    }
  );
    this.recommendation.push({ title: 'Cars', year: '2006', poster: 'https://image.tmdb.org/t/p/original/u4G8EkiIBZYx0wEg2xDlXZigTOZ.jpg' });
    this.recommendation$.next(this.recommendation);
  }
}