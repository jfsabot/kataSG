import { IBankTransaction } from 'app/entities/bank-transaction/bank-transaction.model';

export interface IBankAccount {
  id?: number;
  ownerLogin?: string | null;
  position?: number | null;
  bankTransactions?: IBankTransaction[] | null;
}

export class BankAccount implements IBankAccount {
  constructor(
    public id?: number,
    public ownerLogin?: string | null,
    public position?: number | null,
    public bankTransactions?: IBankTransaction[] | null
  ) {}
}

export function getBankAccountIdentifier(bankAccount: IBankAccount): number | undefined {
  return bankAccount.id;
}
