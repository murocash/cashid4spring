package cash.muro.springsecurity.authentication.cashid.conf;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.AuthenticationException;

import cash.muro.springsecurity.authentication.cashid.service.CashIdCredentialsService;
import cash.muro.springsecurity.authentication.cashid.service.CashIdCredentialsServiceImpl;

class CredentialsServiceTest {
	
	private CashIdCredentialsService credentialsService;

	@BeforeEach
	void initService() {
		credentialsService = new CashIdCredentialsServiceImpl(new AuthKeysConf(Duration.ofMillis(200), 64));
	}

	@Test
	void testWhenLessTimeThanExpireThenGetDifferentNonceForAddress() throws AuthenticationException {
		String address = "bchaddress:abcd";
		String nonce = credentialsService.nonce(address);
		String nonce2 = credentialsService.nonce(address);
		assertNotEquals(nonce, nonce2);
	}

}
