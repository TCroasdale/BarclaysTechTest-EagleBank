package com.techtest.eaglebank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import com.baeldung.openapi.model.CreateTransactionRequest;
import com.baeldung.openapi.model.ListTransactionsResponse;
import com.baeldung.openapi.model.TransactionResponse;
import com.baeldung.openapi.model.CreateBankAccountRequest.AccountTypeEnum;
import com.baeldung.openapi.model.TransactionResponse.CurrencyEnum;
import com.baeldung.openapi.model.TransactionResponse.TypeEnum;
import com.techtest.eaglebank.entities.Account;
import com.techtest.eaglebank.entities.Transaction;
import com.techtest.eaglebank.entities.User;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Collections;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations="classpath:test.properties")
class TransactionITests {

	@Autowired private TestRestTemplate template;
	@Autowired private DatabaseService db;
	@Autowired private JwtService jwtService;
	private String validToken = "";
	private String accountNum;
	private String otheraccountNum;

	@BeforeEach
    public void initTest() throws Exception {
		db.Reset();
		User u = new User();
		u.address = "address";
		u.email = "test.user@eagle.bank";
		u.name = "John Smith";
		u.phoneNumber = "07123456789";
		u.userid = "usr-0";
		u = db.saveUser(u);

		User u2 = new User();
		u2.address = "address";
		u2.email = "test.user@eagle.bank";
		u2.name = "Smoth John";
		u2.phoneNumber = "07123456789";
		u2.userid = "usr-3";
		u2 = db.saveUser(u2);

		Account a = new Account();
		a.ownerid = u.getId();
		a.accountName = "Test Name";
		a.accountType = AccountTypeEnum.PERSONAL;
		a.balance = 100.0;
		a = db.saveAccount(a);
		accountNum = a.accountNumber;

		Account a2 = new Account();
		a2.ownerid = u2.getId();
		a2.accountName = "Test Name 2";
		a2.accountType = AccountTypeEnum.PERSONAL;
		a2.balance = 100.0;
		a2 = db.saveAccount(a2);
		otheraccountNum = a2.accountNumber;

		validToken = jwtService.IssueToken(u);

		template.getRestTemplate().setInterceptors(
			Collections.singletonList((request, body, execution) -> {
				request.getHeaders().add("Authorization", "Bearer " + validToken);
				return execution.execute(request, body);
			}));
    }

	@Test
	public void createDeposit() throws Exception {
		CreateTransactionRequest cbar = new CreateTransactionRequest(10.0, CreateTransactionRequest.CurrencyEnum.GBP, CreateTransactionRequest.TypeEnum.DEPOSIT);

		ResponseEntity<TransactionResponse> response = template.postForEntity("/v1/accounts/" + accountNum + "/transactions", cbar, TransactionResponse.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
		assertThat(response.getBody().getAmount()).isEqualTo(10);
	}

	@Test
	public void createWithdrawal() throws Exception {
		CreateTransactionRequest cbar = new CreateTransactionRequest(10.0, CreateTransactionRequest.CurrencyEnum.GBP, CreateTransactionRequest.TypeEnum.WITHDRAWAL);

		ResponseEntity<TransactionResponse> response = template.postForEntity("/v1/accounts/" + accountNum + "/transactions", cbar, TransactionResponse.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
		assertThat(response.getBody().getAmount()).isEqualTo(10);
	}

	@Test
	public void createWithdrawal_insufficientfunds() throws Exception {
		CreateTransactionRequest cbar = new CreateTransactionRequest(1000.0, CreateTransactionRequest.CurrencyEnum.GBP, CreateTransactionRequest.TypeEnum.WITHDRAWAL);

		ResponseEntity<TransactionResponse> response = template.postForEntity("/v1/accounts/" + accountNum + "/transactions", cbar, TransactionResponse.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(422));
	}

	@Test
	public void createDeposit_nonExistantAccount() throws Exception {
		CreateTransactionRequest cbar = new CreateTransactionRequest(10.0, CreateTransactionRequest.CurrencyEnum.GBP, CreateTransactionRequest.TypeEnum.DEPOSIT);

		ResponseEntity<TransactionResponse> response = template.postForEntity("/v1/accounts/01654321/transactions", cbar, TransactionResponse.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(404));
	}

	@Test
	public void createWithdrawal_nonExistantAccount() throws Exception {
		CreateTransactionRequest cbar = new CreateTransactionRequest(10.0, CreateTransactionRequest.CurrencyEnum.GBP, CreateTransactionRequest.TypeEnum.WITHDRAWAL);

		ResponseEntity<TransactionResponse> response = template.postForEntity("/v1/accounts/01654321/transactions", cbar, TransactionResponse.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(404));
	}

	@Test
	public void createDeposit_nonUserAccount() throws Exception {
		CreateTransactionRequest cbar = new CreateTransactionRequest(10.0, CreateTransactionRequest.CurrencyEnum.GBP, CreateTransactionRequest.TypeEnum.DEPOSIT);

		ResponseEntity<TransactionResponse> response = template.postForEntity("/v1/accounts/" + otheraccountNum + "/transactions", cbar, TransactionResponse.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(403));
	}

	@Test
	public void listTransactions() throws Exception {
		Transaction t = new Transaction();
		t.accountNumber = accountNum;
		t.amount = 10;
		t.currency = CurrencyEnum.GBP;
		t.transfactionType = TypeEnum.DEPOSIT;
		t.reference = "Test reference";
		t = db.saveTransaction(t);

		ResponseEntity<ListTransactionsResponse> response = template.getForEntity("/v1/accounts/" + accountNum + "/transactions", ListTransactionsResponse.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getTransactions().size()).isEqualTo(1);
		assertThat(response.getBody().getTransactions().get(0).getAmount()).isEqualTo(10);
		assertThat(response.getBody().getTransactions().get(0).getReference()).isEqualTo("Test reference");
	}

	@Test
	public void listTransactionsOnOtherAccount() throws Exception {
		ResponseEntity<ListTransactionsResponse> response = template.getForEntity("/v1/accounts/" + otheraccountNum + "/transactions", ListTransactionsResponse.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(403));
	}

	@Test
	public void listTransactionsOnInvalidAccount() throws Exception {
		ResponseEntity<ListTransactionsResponse> response = template.getForEntity("/v1/accounts/01654321/transactions", ListTransactionsResponse.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(404));
	}

}
