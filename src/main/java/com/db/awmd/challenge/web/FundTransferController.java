package com.db.awmd.challenge.web;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Fund;
import com.db.awmd.challenge.exception.InvalidAmountException;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.NotificationService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/transfer")
@Slf4j
public class FundTransferController {

	private final AccountsService accountsService;
	@Autowired
	private NotificationService notificationService;
	private final ReentrantLock lock = new ReentrantLock();
	private static AtomicBoolean atomicBoolean = new AtomicBoolean(false);

	@Autowired
	public FundTransferController(AccountsService accountsService) {
		this.accountsService = accountsService;
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> transferFunds(@RequestBody @Valid Fund fund) throws InterruptedException {
		log.info("Tranfer fund {}", fund);
		lock.lock();
		try {
			if (this.accountsService.updateFromAccount(fund.getFromAccountId(), fund.getBalance()) != null) {
				atomicBoolean.set(true);
				String transferDescription = "Amount " + fund.getBalance()
						+ " is debited from your account and is transferred to " + fund.getToAccountId();
				Account account = this.accountsService.getAccount(fund.getFromAccountId());
				notificationService.notifyAboutTransfer(account, transferDescription);
			}
			if (atomicBoolean.get())
				this.accountsService.updateToAccount(fund.getToAccountId(), fund.getBalance());
			String transferDescription = "Amount " + fund.getBalance()
					+ " is credited in your account and is transferred from " + fund.getFromAccountId();
			Account account = this.accountsService.getAccount(fund.getFromAccountId());
			notificationService.notifyAboutTransfer(account, transferDescription);
		} catch (InvalidAmountException inae) {
			return new ResponseEntity<>(inae.getMessage(), HttpStatus.BAD_REQUEST);
		} finally {
			lock.unlock();
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
