import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IBankTransaction, getBankTransactionIdentifier } from '../bank-transaction.model';

export type EntityResponseType = HttpResponse<IBankTransaction>;
export type EntityArrayResponseType = HttpResponse<IBankTransaction[]>;

@Injectable({ providedIn: 'root' })
export class BankTransactionService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/bank-transactions');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(bankTransaction: IBankTransaction): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(bankTransaction);
    return this.http
      .post<IBankTransaction>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(bankTransaction: IBankTransaction): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(bankTransaction);
    return this.http
      .put<IBankTransaction>(`${this.resourceUrl}/${getBankTransactionIdentifier(bankTransaction) as number}`, copy, {
        observe: 'response',
      })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(bankTransaction: IBankTransaction): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(bankTransaction);
    return this.http
      .patch<IBankTransaction>(`${this.resourceUrl}/${getBankTransactionIdentifier(bankTransaction) as number}`, copy, {
        observe: 'response',
      })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IBankTransaction>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IBankTransaction[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addBankTransactionToCollectionIfMissing(
    bankTransactionCollection: IBankTransaction[],
    ...bankTransactionsToCheck: (IBankTransaction | null | undefined)[]
  ): IBankTransaction[] {
    const bankTransactions: IBankTransaction[] = bankTransactionsToCheck.filter(isPresent);
    if (bankTransactions.length > 0) {
      const bankTransactionCollectionIdentifiers = bankTransactionCollection.map(
        bankTransactionItem => getBankTransactionIdentifier(bankTransactionItem)!
      );
      const bankTransactionsToAdd = bankTransactions.filter(bankTransactionItem => {
        const bankTransactionIdentifier = getBankTransactionIdentifier(bankTransactionItem);
        if (bankTransactionIdentifier == null || bankTransactionCollectionIdentifiers.includes(bankTransactionIdentifier)) {
          return false;
        }
        bankTransactionCollectionIdentifiers.push(bankTransactionIdentifier);
        return true;
      });
      return [...bankTransactionsToAdd, ...bankTransactionCollection];
    }
    return bankTransactionCollection;
  }

  protected convertDateFromClient(bankTransaction: IBankTransaction): IBankTransaction {
    return Object.assign({}, bankTransaction, {
      valueDate: bankTransaction.valueDate?.isValid() ? bankTransaction.valueDate.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.valueDate = res.body.valueDate ? dayjs(res.body.valueDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((bankTransaction: IBankTransaction) => {
        bankTransaction.valueDate = bankTransaction.valueDate ? dayjs(bankTransaction.valueDate) : undefined;
      });
    }
    return res;
  }
}
