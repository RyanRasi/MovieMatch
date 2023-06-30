import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MovieSelectorComponent } from './movie-selector.component';

describe('MovieSelectorComponent', () => {
  let component: MovieSelectorComponent;
  let fixture: ComponentFixture<MovieSelectorComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MovieSelectorComponent]
    });
    fixture = TestBed.createComponent(MovieSelectorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
