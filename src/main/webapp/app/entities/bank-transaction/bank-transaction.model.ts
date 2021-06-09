import * as dayjs from 'dayjs';
import { IBankAccount } from 'app/entities/bank-account/bank-account.model';
import { TransactionType } from 'app/entities/enumerations/transaction-type.model';

export interface IBankTransaction {
  id?: number;
  label?: string | null;
  valueDate?: dayjs.Dayjs | null;
  amount?: number | null;
  type?: TransactionType | null;
  account?: IBankAccount | null;
}

export class BankTransaction implements IBankTransaction {
  constructor(
    public id?: number,
    public label?: string | null,
    public valueDate?: dayjs.Dayjs | null,
    public amount?: number | null,
    public type?: TransactionType | null,
    public account?: IBankAccount | null
  ) {}
}

export function getBankTransactionIdentifier(bankTransaction: IBankTransaction): number | undefined {
  return bankTransaction.id;
}
