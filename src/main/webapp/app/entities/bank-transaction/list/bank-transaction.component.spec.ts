import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { BankTransactionService } from '../service/bank-transaction.service';

import { BankTransactionComponent } from './bank-transaction.component';

describe('Component Tests', () => {
  describe('BankTransaction Management Component', () => {
    let comp: BankTransactionComponent;
    let fixture: ComponentFixture<BankTransactionComponent>;
    let service: BankTransactionService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [BankTransactionComponent],
      })
        .overrideTemplate(BankTransactionComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(BankTransactionComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(BankTransactionService);

      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [{ id: 123 }],
            headers,
          })
        )
      );
    });

    it('Should call load all on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.bankTransactions?.[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
