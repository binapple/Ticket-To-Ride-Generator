import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectedMapComponent } from './selected-map.component';

describe('SelectedMapComponent', () => {
  let component: SelectedMapComponent;
  let fixture: ComponentFixture<SelectedMapComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SelectedMapComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SelectedMapComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
