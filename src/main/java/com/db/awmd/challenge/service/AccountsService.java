package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.InvalidAmountException;
import com.db.awmd.challenge.repository.AccountsRepository;
import lombok.Getter;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountsService {

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
		  throw new InvalidAmountException("Insufficient Fund");
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
}
