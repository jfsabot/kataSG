import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IBankTransaction, BankTransaction } from '../bank-transaction.model';
import { BankTransactionService } from '../service/bank-transaction.service';
import { IBankAccount } from 'app/entities/bank-account/bank-account.model';
import { BankAccountService } from 'app/entities/bank-account/service/bank-account.service';

@Component({
  selector: 'jhi-bank-transaction-update',
  templateUrl: './bank-transaction-update.component.html',
})
export class BankTransactionUpdateComponent implements OnInit {
  isSaving = false;

  bankAccountsSharedCollection: IBankAccount[] = [];

  editForm = this.fb.group({
    id: [],
    label: [],
    valueDate: [],
    amount: [],
    type: [],
    account: [],
  });

  constructor(
    protected bankTransactionService: BankTransactionService,
    protected bankAccountService: BankAccountService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ bankTransaction }) => {
      this.updateForm(bankTransaction);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const bankTransaction = this.createFromForm();
    if (bankTransaction.id !== undefined) {
      this.subscribeToSaveResponse(this.bankTransactionService.update(bankTransaction));
    } else {
      this.subscribeToSaveResponse(this.bankTransactionService.create(bankTransaction));
    }
  }

  trackBankAccountById(index: number, item: IBankAccount): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBankTransaction>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(bankTransaction: IBankTransaction): void {
    this.editForm.patchValue({
      id: bankTransaction.id,
      label: bankTransaction.label,
      valueDate: bankTransaction.valueDate,
      amount: bankTransaction.amount,
      type: bankTransaction.type,
      account: bankTransaction.account,
    });

    this.bankAccountsSharedCollection = this.bankAccountService.addBankAccountToCollectionIfMissing(
      this.bankAccountsSharedCollection,
      bankTransaction.account
    );
  }

  protected loadRelationshipsOptions(): void {
    this.bankAccountService
      .query()
      .pipe(map((res: HttpResponse<IBankAccount[]>) => res.body ?? []))
      .pipe(
        map((bankAccounts: IBankAccount[]) =>
          this.bankAccountService.addBankAccountToCollectionIfMissing(bankAccounts, this.editForm.get('account')!.value)
        )
      )
      .subscribe((bankAccounts: IBankAccount[]) => (this.bankAccountsSharedCollection = bankAccounts));
  }

  protected createFromForm(): IBankTransaction {
    return {
      ...new BankTransaction(),
      id: this.editForm.get(['id'])!.value,
      label: this.editForm.get(['label'])!.value,
      valueDate: this.editForm.get(['valueDate'])!.value,
      amount: this.editForm.get(['amount'])!.value,
      type: this.editForm.get(['type'])!.value,
      account: this.editForm.get(['account'])!.value,
    };
  }
}
