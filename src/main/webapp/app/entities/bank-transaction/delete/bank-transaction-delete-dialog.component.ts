import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IBankTransaction } from '../bank-transaction.model';
import { BankTransactionService } from '../service/bank-transaction.service';

@Component({
  templateUrl: './bank-transaction-delete-dialog.component.html',
})
export class BankTransactionDeleteDialogComponent {
  bankTransaction?: IBankTransaction;

  constructor(protected bankTransactionService: BankTransactionService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.bankTransactionService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
