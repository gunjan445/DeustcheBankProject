package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.service.AccountsService;
import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class FundTransferControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private AccountsService accountsService;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	public void prepareMockMvc() {
		this.mockMvc = webAppContextSetup(this.webApplicationContext).build();

		// Reset the existing accounts before each test.
		accountsService.getAccountsRepository().clearAccounts();
	}

	@Test
	public void createAccount1() throws Exception {
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());

		Account account = accountsService.getAccount("Id-123");
		assertThat(account.getAccountId()).isEqualTo("Id-123");
		assertThat(account.getBalance()).isEqualByComparingTo("1000");
	}

	@Test
	public void createAccount2() throws Exception {
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-133\",\"balance\":1000}")).andExpect(status().isCreated());

		Account account = accountsService.getAccount("Id-133");
		assertThat(account.getAccountId()).isEqualTo("Id-133");
		assertThat(account.getBalance()).isEqualByComparingTo("1000");
	}

	@Test
	public void transferFunds() throws Exception {
		createAccount1();
		createAccount2();
		Account fromAccount_initial = this.accountsService.getAccount("Id-123");
		Account toAccount_initial = this.accountsService.getAccount("Id-133");
		this.mockMvc
				.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
						.content("{\"fromAccountId\":\"Id-123\",\"toAccountId\":\"Id-133\",\"balance\":100}"))
				.andExpect(status().isOk());
		Account fromAccount = this.accountsService.getAccount("Id-123");
		Account toAccount = this.accountsService.getAccount("Id-133");
		assertThat(fromAccount_initial.getBalance().compareTo(fromAccount.getBalance()) == -1);
		assertThat(toAccount_initial.getBalance().compareTo(toAccount.getBalance()) == 1);
	}

	@SuppressWarnings("unused")
	@Test
	public void notSufficientAmountTotransferFunds() throws Exception {
		createAccount1();
		createAccount2();
		Account fromAccount_initial = this.accountsService.getAccount("Id-123");
		Account toAccount_initial = this.accountsService.getAccount("Id-133");
		this.mockMvc
				.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
						.content("{\"fromAccountId\":\"Id-123\",\"toAccountId\":\"Id-133\",\"balance\":1000}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void getAccount() throws Exception {
		String uniqueAccountId = "Id-" + System.currentTimeMillis();
		Account account = new Account(uniqueAccountId, new BigDecimal("123.45"));
		this.accountsService.createAccount(account);
		this.mockMvc.perform(get("/v1/accounts/" + uniqueAccountId)).andExpect(status().isOk())
				.andExpect(content().string("{\"accountId\":\"" + uniqueAccountId + "\",\"balance\":123.45}"));
	}
}
