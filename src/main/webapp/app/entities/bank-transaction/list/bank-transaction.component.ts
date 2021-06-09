import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IBankTransaction } from '../bank-transaction.model';
import { BankTransactionService } from '../service/bank-transaction.service';
import { BankTransactionDeleteDialogComponent } from '../delete/bank-transaction-delete-dialog.component';

@Component({
  selector: 'jhi-bank-transaction',
  templateUrl: './bank-transaction.component.html',
})
export class BankTransactionComponent implements OnInit {
  bankTransactions?: IBankTransaction[];
  isLoading = false;

  constructor(protected bankTransactionService: BankTransactionService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.bankTransactionService.query().subscribe(
      (res: HttpResponse<IBankTransaction[]>) => {
        this.isLoading = false;
        this.bankTransactions = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IBankTransaction): number {
    return item.id!;
  }

  delete(bankTransaction: IBankTransaction): void {
    const modalRef = this.modalService.open(BankTransactionDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.bankTransaction = bankTransaction;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
