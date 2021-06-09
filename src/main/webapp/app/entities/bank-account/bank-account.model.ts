import { IBankTransaction } from 'app/entities/bank-transaction/bank-transaction.model';

export interface IBankAccount {
  id?: number;
  ownerName?: string | null;
  position?: number | null;
  bankTransactions?: IBankTransaction[] | null;
}

export class BankAccount implements IBankAccount {
  constructor(
    public id?: number,
    public ownerName?: string | null,
    public position?: number | null,
    public bankTransactions?: IBankTransaction[] | null
  ) {}
}

export function getBankAccountIdentifier(bankAccount: IBankAccount): number | undefined {
  return bankAccount.id;
}
