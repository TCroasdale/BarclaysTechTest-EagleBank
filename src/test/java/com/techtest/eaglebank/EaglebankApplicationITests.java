package com.techtest.eaglebank;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import com.baeldung.openapi.model.CreateUserRequest;
import com.baeldung.openapi.model.CreateUserRequestAddress;
import com.baeldung.openapi.model.UserResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EaglebankApplicationITests {

	@Autowired private TestRestTemplate template;

	@Test
	public void createUser_Success() throws Exception {
		CreateUserRequestAddress addr = new CreateUserRequestAddress("123 fake street", "manchester", "greater manchester", "M1 123");
		CreateUserRequest cur = new CreateUserRequest("Test Name", addr, "07123456789", "test.name@eagle.bank");

		ResponseEntity<UserResponse> response = template.postForEntity("/v1/users", cur, UserResponse.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
		assertThat(response.getBody()).hasFieldOrProperty("name");
		assertThat(response.getBody().getName()).isEqualTo("Test Name");
		assertThat(response.getBody()).hasFieldOrProperty("email");
		assertThat(response.getBody().getEmail()).isEqualTo("test.name@eagle.bank");
		assertThat(response.getBody()).hasFieldOrProperty("phoneNumber");
		assertThat(response.getBody().getPhoneNumber()).isEqualTo("07123456789");
		assertThat(response.getBody()).hasFieldOrProperty("address");
	}

	@Test
	public void createUser_BadRequest() throws Exception {
		// Missing name
		CreateUserRequestAddress addr = new CreateUserRequestAddress("123 fake street", "manchester", "greater manchester", "M1 123");
		CreateUserRequest cur = new CreateUserRequest("", addr, "07123456789", "test.name@eagle.bank");

		ResponseEntity<UserResponse> response = template.postForEntity("/v1/users", cur, UserResponse.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(400));

		// Missing phoneNumber
		cur = new CreateUserRequest("Test Name", addr, "", "test.name@eagle.bank");
		response = template.postForEntity("/v1/users", cur, UserResponse.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(400));

		// Missing email
		cur = new CreateUserRequest("Test Name", addr, "07123456789", "");
		response = template.postForEntity("/v1/users", cur, UserResponse.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(400));

		// Missing line 1
		addr = new CreateUserRequestAddress("", "manchester", "greater manchester", "M1 123");
		cur = new CreateUserRequest("Test Name", addr, "07123456789", "test.name@eagle.bank");
		response = template.postForEntity("/v1/users", cur, UserResponse.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(400));

		// Missing town
		addr = new CreateUserRequestAddress("123 test street", "", "greater manchester", "M1 123");
		cur = new CreateUserRequest("Test Name", addr, "07123456789", "test.name@eagle.bank");
		response = template.postForEntity("/v1/users", cur, UserResponse.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(400));

		// Missing county
		addr = new CreateUserRequestAddress("123 test street", "manchester", "", "M1 123");
		cur = new CreateUserRequest("Test Name", addr, "07123456789", "test.name@eagle.bank");
		response = template.postForEntity("/v1/users", cur, UserResponse.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(400));

		// Missing postcode
		addr = new CreateUserRequestAddress("123 test street", "manchester", "greater manchester", "");
		cur = new CreateUserRequest("Test Name", addr, "07123456789", "test.name@eagle.bank");
		response = template.postForEntity("/v1/users", cur, UserResponse.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(400));
	}

	

}
