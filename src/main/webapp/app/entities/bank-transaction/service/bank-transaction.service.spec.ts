import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { TransactionType } from 'app/entities/enumerations/transaction-type.model';
import { IBankTransaction, BankTransaction } from '../bank-transaction.model';

import { BankTransactionService } from './bank-transaction.service';

describe('Service Tests', () => {
  describe('BankTransaction Service', () => {
    let service: BankTransactionService;
    let httpMock: HttpTestingController;
    let elemDefault: IBankTransaction;
    let expectedResult: IBankTransaction | IBankTransaction[] | boolean | null;
    let currentDate: dayjs.Dayjs;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(BankTransactionService);
      httpMock = TestBed.inject(HttpTestingController);
      currentDate = dayjs();

      elemDefault = {
        id: 0,
        label: 'AAAAAAA',
        valueDate: currentDate,
        amount: 0,
        type: TransactionType.CREDIT,
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            valueDate: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a BankTransaction', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            valueDate: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            valueDate: currentDate,
          },
          returnedFromService
        );

        service.create(new BankTransaction()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a BankTransaction', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            label: 'BBBBBB',
            valueDate: currentDate.format(DATE_FORMAT),
            amount: 1,
            type: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            valueDate: currentDate,
          },
          returnedFromService
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a BankTransaction', () => {
        const patchObject = Object.assign(
          {
            valueDate: currentDate.format(DATE_FORMAT),
            type: 'BBBBBB',
          },
          new BankTransaction()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign(
          {
            valueDate: currentDate,
          },
          returnedFromService
        );

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of BankTransaction', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            label: 'BBBBBB',
            valueDate: currentDate.format(DATE_FORMAT),
            amount: 1,
            type: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            valueDate: currentDate,
          },
          returnedFromService
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a BankTransaction', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addBankTransactionToCollectionIfMissing', () => {
        it('should add a BankTransaction to an empty array', () => {
          const bankTransaction: IBankTransaction = { id: 123 };
          expectedResult = service.addBankTransactionToCollectionIfMissing([], bankTransaction);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(bankTransaction);
        });

        it('should not add a BankTransaction to an array that contains it', () => {
          const bankTransaction: IBankTransaction = { id: 123 };
          const bankTransactionCollection: IBankTransaction[] = [
            {
              ...bankTransaction,
            },
            { id: 456 },
          ];
          expectedResult = service.addBankTransactionToCollectionIfMissing(bankTransactionCollection, bankTransaction);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a BankTransaction to an array that doesn't contain it", () => {
          const bankTransaction: IBankTransaction = { id: 123 };
          const bankTransactionCollection: IBankTransaction[] = [{ id: 456 }];
          expectedResult = service.addBankTransactionToCollectionIfMissing(bankTransactionCollection, bankTransaction);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(bankTransaction);
        });

        it('should add only unique BankTransaction to an array', () => {
          const bankTransactionArray: IBankTransaction[] = [{ id: 123 }, { id: 456 }, { id: 81895 }];
          const bankTransactionCollection: IBankTransaction[] = [{ id: 123 }];
          expectedResult = service.addBankTransactionToCollectionIfMissing(bankTransactionCollection, ...bankTransactionArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const bankTransaction: IBankTransaction = { id: 123 };
          const bankTransaction2: IBankTransaction = { id: 456 };
          expectedResult = service.addBankTransactionToCollectionIfMissing([], bankTransaction, bankTransaction2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(bankTransaction);
          expect(expectedResult).toContain(bankTransaction2);
        });

        it('should accept null and undefined values', () => {
          const bankTransaction: IBankTransaction = { id: 123 };
          expectedResult = service.addBankTransactionToCollectionIfMissing([], null, bankTransaction, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(bankTransaction);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
