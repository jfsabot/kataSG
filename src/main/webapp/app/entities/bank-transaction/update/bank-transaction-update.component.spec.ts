jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { BankTransactionService } from '../service/bank-transaction.service';
import { IBankTransaction, BankTransaction } from '../bank-transaction.model';
import { IBankAccount } from 'app/entities/bank-account/bank-account.model';
import { BankAccountService } from 'app/entities/bank-account/service/bank-account.service';

import { BankTransactionUpdateComponent } from './bank-transaction-update.component';

describe('Component Tests', () => {
  describe('BankTransaction Management Update Component', () => {
    let comp: BankTransactionUpdateComponent;
    let fixture: ComponentFixture<BankTransactionUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let bankTransactionService: BankTransactionService;
    let bankAccountService: BankAccountService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [BankTransactionUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(BankTransactionUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(BankTransactionUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      bankTransactionService = TestBed.inject(BankTransactionService);
      bankAccountService = TestBed.inject(BankAccountService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call BankAccount query and add missing value', () => {
        const bankTransaction: IBankTransaction = { id: 456 };
        const account: IBankAccount = { id: 47917 };
        bankTransaction.account = account;

        const bankAccountCollection: IBankAccount[] = [{ id: 17676 }];
        spyOn(bankAccountService, 'query').and.returnValue(of(new HttpResponse({ body: bankAccountCollection })));
        const additionalBankAccounts = [account];
        const expectedCollection: IBankAccount[] = [...additionalBankAccounts, ...bankAccountCollection];
        spyOn(bankAccountService, 'addBankAccountToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ bankTransaction });
        comp.ngOnInit();

        expect(bankAccountService.query).toHaveBeenCalled();
        expect(bankAccountService.addBankAccountToCollectionIfMissing).toHaveBeenCalledWith(
          bankAccountCollection,
          ...additionalBankAccounts
        );
        expect(comp.bankAccountsSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const bankTransaction: IBankTransaction = { id: 456 };
        const account: IBankAccount = { id: 4944 };
        bankTransaction.account = account;

        activatedRoute.data = of({ bankTransaction });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(bankTransaction));
        expect(comp.bankAccountsSharedCollection).toContain(account);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const bankTransaction = { id: 123 };
        spyOn(bankTransactionService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ bankTransaction });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: bankTransaction }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(bankTransactionService.update).toHaveBeenCalledWith(bankTransaction);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const bankTransaction = new BankTransaction();
        spyOn(bankTransactionService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ bankTransaction });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: bankTransaction }));
        saveSubject.complete();

        // THEN
        expect(bankTransactionService.create).toHaveBeenCalledWith(bankTransaction);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const bankTransaction = { id: 123 };
        spyOn(bankTransactionService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ bankTransaction });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(bankTransactionService.update).toHaveBeenCalledWith(bankTransaction);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackBankAccountById', () => {
        it('Should return tracked BankAccount primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackBankAccountById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
