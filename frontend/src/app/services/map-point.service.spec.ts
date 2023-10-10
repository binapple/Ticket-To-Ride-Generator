import { TestBed } from '@angular/core/testing';

import { MapPointService } from './map-point.service';

describe('MapPointService', () => {
  let service: MapPointService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MapPointService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
