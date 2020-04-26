package io.costax.trading.finex.boundary;

import io.costax.trading.finex.control.AccountRepository;
import io.costax.trading.finex.entity.Account;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.UUID;

@Stateless
public class TradingService {

    @Inject
    AccountRepository accountRepository;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Account createAccount(final Account account) {
        Account newOne = accountRepository.save(account.copy());
        accountRepository.flush();

        return newOne;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void addMoney(final UUID id, final BigDecimal value) {
        accountRepository
                .findById(id)
                .ifPresent(account -> account.deposit(value));
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void removeMoney(final UUID id, final BigDecimal value) {
        accountRepository
                .findById(id)
                .ifPresent(account -> account.raise(value));
    }


}
