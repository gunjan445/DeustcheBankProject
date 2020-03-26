package com.db.awmd.challenge.web;

import java.util.concurrent.TimeUnit;
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

import com.db.awmd.challenge.domain.Fund;
import com.db.awmd.challenge.exception.InvalidAmountException;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.NotificationService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/transfer")
@Slf4j
@Api(value = "Fund Transfer Resource")
public class FundTransferController {

	private final AccountsService accountsService;
	private final ReentrantLock lock = new ReentrantLock();

	@Autowired
	public FundTransferController(AccountsService accountsService) {
		this.accountsService = accountsService;
	}
	 @ApiOperation(value = "Transfer the funds")
	    @ApiResponses(
	            value = {
	                    @ApiResponse(code = 100, message = "100 is the message"),
	                    @ApiResponse(code = 200, message = "Successful Hello World")
	            }
    )
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> transferFunds(@RequestBody @Valid Fund fund) {
		log.info("Tranfer fund {}", fund);
		try {
			lock.tryLock(300, TimeUnit.SECONDS);
			accountsService.transferFund(fund);
		} catch (InvalidAmountException | InterruptedException inae) {
			return new ResponseEntity<>(inae.getMessage(), HttpStatus.BAD_REQUEST); 
		} finally {
			lock.unlock();
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
