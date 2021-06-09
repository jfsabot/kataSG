import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IBankTransaction, BankTransaction } from '../bank-transaction.model';
import { BankTransactionService } from '../service/bank-transaction.service';

@Injectable({ providedIn: 'root' })
export class BankTransactionRoutingResolveService implements Resolve<IBankTransaction> {
  constructor(protected service: BankTransactionService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IBankTransaction> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((bankTransaction: HttpResponse<BankTransaction>) => {
          if (bankTransaction.body) {
            return of(bankTransaction.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new BankTransaction());
  }
}
