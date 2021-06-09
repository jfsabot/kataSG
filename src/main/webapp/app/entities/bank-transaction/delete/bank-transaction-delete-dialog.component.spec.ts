jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { BankTransactionService } from '../service/bank-transaction.service';

import { BankTransactionDeleteDialogComponent } from './bank-transaction-delete-dialog.component';

describe('Component Tests', () => {
  describe('BankTransaction Management Delete Component', () => {
    let comp: BankTransactionDeleteDialogComponent;
    let fixture: ComponentFixture<BankTransactionDeleteDialogComponent>;
    let service: BankTransactionService;
    let mockActiveModal: NgbActiveModal;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [BankTransactionDeleteDialogComponent],
        providers: [NgbActiveModal],
      })
        .overrideTemplate(BankTransactionDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(BankTransactionDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(BankTransactionService);
      mockActiveModal = TestBed.inject(NgbActiveModal);
    });

    describe('confirmDelete', () => {
      it('Should call delete service on confirmDelete', inject(
        [],
        fakeAsync(() => {
          // GIVEN
          spyOn(service, 'delete').and.returnValue(of({}));

          // WHEN
          comp.confirmDelete(123);
          tick();

          // THEN
          expect(service.delete).toHaveBeenCalledWith(123);
          expect(mockActiveModal.close).toHaveBeenCalledWith('deleted');
        })
      ));

      it('Should not call delete service on clear', () => {
        // GIVEN
        spyOn(service, 'delete');

        // WHEN
        comp.cancel();

        // THEN
        expect(service.delete).not.toHaveBeenCalled();
        expect(mockActiveModal.close).not.toHaveBeenCalled();
        expect(mockActiveModal.dismiss).toHaveBeenCalled();
      });
    });
  });
});
