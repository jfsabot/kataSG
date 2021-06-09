import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IBankTransaction } from '../bank-transaction.model';

@Component({
  selector: 'jhi-bank-transaction-detail',
  templateUrl: './bank-transaction-detail.component.html',
})
export class BankTransactionDetailComponent implements OnInit {
  bankTransaction: IBankTransaction | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ bankTransaction }) => {
      this.bankTransaction = bankTransaction;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
