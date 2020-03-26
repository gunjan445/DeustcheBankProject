package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Fund;
import com.db.awmd.challenge.exception.InsufficientFundException;
import com.db.awmd.challenge.repository.AccountsRepository;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountsService {
	private static final String DEBIT_INFO = "is debited from your account and is transferred to ";
	private static final String CREDIT_INFO = "is credited in your account and is transferred from ";
	private static final String AMOUNT ="Amount ";
	private static AtomicBoolean atomicBoolean = new AtomicBoolean(false);
	@Autowired
	private NotificationService notificationService;
  @Getter
  private final AccountsRepository accountsRepository;

  @Autowired
  public AccountsService(AccountsRepository accountsRepository) {
    this.accountsRepository = accountsRepository;
  }

  public void createAccount(Account account) {
    this.accountsRepository.createAccount(account);
  }

  public Account getAccount(String accountId) {
    return this.accountsRepository.getAccount(accountId);
  }
  public Account updateFromAccount(String accountId,BigDecimal balance) {
	  Account account = getAccount(accountId);
	  if(account.getBalance().compareTo(balance)==-1) {
		  throw new InsufficientFundException("Insufficient Fund");
	  }
	  else
	  account.setBalance(account.getBalance().subtract(balance));
	return this.accountsRepository.updateAccount(account);
	  
  }
  public Account updateToAccount(String accountId,BigDecimal balance) {
	  Account account = getAccount(accountId);
	  account.setBalance(account.getBalance().add(balance));
	return this.accountsRepository.updateAccount(account);
	  
  }
  public void transferFund(Fund fund) {
	  if (updateFromAccount(fund.getFromAccountId(), fund.getBalance()) != null) {
			atomicBoolean.set(true);
			String transferDescription = AMOUNT + fund.getBalance()
					+ DEBIT_INFO + fund.getToAccountId();
			Account account = getAccount(fund.getFromAccountId());
			notificationService.notifyAboutTransfer(account, transferDescription);
		}
	  if (atomicBoolean.get())
			updateToAccount(fund.getToAccountId(), fund.getBalance());
		String transferDescription = AMOUNT + fund.getBalance()
				+ CREDIT_INFO + fund.getFromAccountId();
		Account account = getAccount(fund.getFromAccountId());
		notificationService.notifyAboutTransfer(account, transferDescription);
  }
}
