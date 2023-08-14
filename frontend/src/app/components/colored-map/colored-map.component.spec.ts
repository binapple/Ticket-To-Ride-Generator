import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ColoredMapComponent } from './colored-map.component';

describe('ColoredMapComponent', () => {
  let component: ColoredMapComponent;
  let fixture: ComponentFixture<ColoredMapComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ColoredMapComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ColoredMapComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
