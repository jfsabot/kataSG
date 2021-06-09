import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { BankTransactionComponent } from '../list/bank-transaction.component';
import { BankTransactionDetailComponent } from '../detail/bank-transaction-detail.component';
import { BankTransactionUpdateComponent } from '../update/bank-transaction-update.component';
import { BankTransactionRoutingResolveService } from './bank-transaction-routing-resolve.service';

const bankTransactionRoute: Routes = [
  {
    path: '',
    component: BankTransactionComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: BankTransactionDetailComponent,
    resolve: {
      bankTransaction: BankTransactionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: BankTransactionUpdateComponent,
    resolve: {
      bankTransaction: BankTransactionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: BankTransactionUpdateComponent,
    resolve: {
      bankTransaction: BankTransactionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(bankTransactionRoute)],
  exports: [RouterModule],
})
export class BankTransactionRoutingModule {}
