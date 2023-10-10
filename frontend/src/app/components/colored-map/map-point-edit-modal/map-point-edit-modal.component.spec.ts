import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MapPointEditModalComponent } from './map-point-edit-modal.component';

describe('MapPointEditModalComponent', () => {
  let component: MapPointEditModalComponent;
  let fixture: ComponentFixture<MapPointEditModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MapPointEditModalComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MapPointEditModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
