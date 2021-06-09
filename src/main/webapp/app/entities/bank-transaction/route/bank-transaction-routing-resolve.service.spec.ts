jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IBankTransaction, BankTransaction } from '../bank-transaction.model';
import { BankTransactionService } from '../service/bank-transaction.service';

import { BankTransactionRoutingResolveService } from './bank-transaction-routing-resolve.service';

describe('Service Tests', () => {
  describe('BankTransaction routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: BankTransactionRoutingResolveService;
    let service: BankTransactionService;
    let resultBankTransaction: IBankTransaction | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(BankTransactionRoutingResolveService);
      service = TestBed.inject(BankTransactionService);
      resultBankTransaction = undefined;
    });

    describe('resolve', () => {
      it('should return IBankTransaction returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultBankTransaction = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultBankTransaction).toEqual({ id: 123 });
      });

      it('should return new IBankTransaction if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultBankTransaction = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultBankTransaction).toEqual(new BankTransaction());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultBankTransaction = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultBankTransaction).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
