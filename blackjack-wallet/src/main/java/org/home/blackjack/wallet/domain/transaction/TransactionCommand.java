package org.home.blackjack.wallet.domain.transaction;

import org.home.blackjack.util.ddd.pattern.ValueObject;
import org.home.blackjack.util.ddd.util.Validator;
import org.home.blackjack.wallet.domain.wallet.CashAmount;

public class TransactionCommand extends ValueObject {
	
	private final TransactionType type;
	private final CashAmount amount;
	private TransactionId id;

	public TransactionCommand(TransactionId id, TransactionType type, CashAmount amount) {
		Validator.notNull(id, type, amount);
		this.id = id;
		this.type = type;
		this.amount = amount;
	}

	public boolean isDebit() {
		return type.equals(TransactionType.DEBIT);
	}

	public CashAmount amount() {
		return amount;
	}

	public TransactionId id() {
		return id;
	}

}
