import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { BankTransactionDetailComponent } from './bank-transaction-detail.component';

describe('Component Tests', () => {
  describe('BankTransaction Management Detail Component', () => {
    let comp: BankTransactionDetailComponent;
    let fixture: ComponentFixture<BankTransactionDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [BankTransactionDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ bankTransaction: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(BankTransactionDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(BankTransactionDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load bankTransaction on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.bankTransaction).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
