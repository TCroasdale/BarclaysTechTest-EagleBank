package com.techtest.eaglebank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import com.baeldung.openapi.model.BankAccountResponse;
import com.baeldung.openapi.model.CreateBankAccountRequest;
import com.baeldung.openapi.model.CreateBankAccountRequest.AccountTypeEnum;
import com.techtest.eaglebank.entities.User;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Collections;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations="classpath:test.properties")
class AccountITests {

	@Autowired private TestRestTemplate template;
	@Autowired private DatabaseService db;
	@Autowired private JwtService jwtService;
	private String validToken = "";

	@BeforeEach
    public void initTest() throws Exception {
		db.Reset();
		User u = new User();
		u.address = "address";
		u.email = "test.user@eagle.bank";
		u.name = "John Smith";
		u.phoneNumber = "07123456789";
		u.userid = "usr-0";
		db.saveUser(u);

		validToken = jwtService.IssueToken(u);

		template.getRestTemplate().setInterceptors(
			Collections.singletonList((request, body, execution) -> {
				request.getHeaders().add("Authorization", "Bearer " + validToken);
				return execution.execute(request, body);
			}));
    }

	@Test
	public void createAccountSuccess() throws Exception {
		CreateBankAccountRequest cbar = new CreateBankAccountRequest("Test name", AccountTypeEnum.PERSONAL);

		ResponseEntity<BankAccountResponse> response = template.postForEntity("/v1/accounts", cbar, BankAccountResponse.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
		assertThat(response.getBody().getAccountType().getValue()).isEqualTo("personal");
		assertThat(response.getBody().getBalance()).isEqualTo(0.0);
		assertThat(response.getBody().getName()).isEqualTo("Test name");
		assertThat(response.getBody().getCurrency().getValue()).isEqualTo("GBP");
		assertThat(response.getBody().getSortCode().getValue()).isEqualTo("10-10-10");
	}

	@Test
	public void createAccountValidation() throws Exception {
		CreateBankAccountRequest cbar = new CreateBankAccountRequest("", AccountTypeEnum.PERSONAL);

		ResponseEntity<BankAccountResponse> response = template.postForEntity("/v1/accounts", cbar, BankAccountResponse.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(400));
	}
}
