import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { BankTransactionComponent } from './list/bank-transaction.component';
import { BankTransactionDetailComponent } from './detail/bank-transaction-detail.component';
import { BankTransactionUpdateComponent } from './update/bank-transaction-update.component';
import { BankTransactionDeleteDialogComponent } from './delete/bank-transaction-delete-dialog.component';
import { BankTransactionRoutingModule } from './route/bank-transaction-routing.module';

@NgModule({
  imports: [SharedModule, BankTransactionRoutingModule],
  declarations: [
    BankTransactionComponent,
    BankTransactionDetailComponent,
    BankTransactionUpdateComponent,
    BankTransactionDeleteDialogComponent,
  ],
  entryComponents: [BankTransactionDeleteDialogComponent],
})
export class BankTransactionModule {}
