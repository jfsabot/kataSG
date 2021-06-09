import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'bank-account',
        data: { pageTitle: 'kataSgApp.bankAccount.home.title' },
        loadChildren: () => import('./bank-account/bank-account.module').then(m => m.BankAccountModule),
      },
      {
        path: 'bank-transaction',
        data: { pageTitle: 'kataSgApp.bankTransaction.home.title' },
        loadChildren: () => import('./bank-transaction/bank-transaction.module').then(m => m.BankTransactionModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
