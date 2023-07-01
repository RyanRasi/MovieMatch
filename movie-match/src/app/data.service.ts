import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable()
export class DataService {
  private data: any[] = [];
  public data$ = new BehaviorSubject<any[]>([]);

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
    this.data = [];
    this.data$.next(this.data);
  }
}