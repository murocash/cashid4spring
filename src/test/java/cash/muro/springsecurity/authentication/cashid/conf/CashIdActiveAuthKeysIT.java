package cash.muro.springsecurity.authentication.cashid.conf;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.AuthenticationException;

import cash.muro.springsecurity.authentication.cashid.conf.AuthKeysConf;
import cash.muro.springsecurity.authentication.cashid.service.CashIdCredentialsService;
import cash.muro.springsecurity.authentication.cashid.service.CashIdCredentialsServiceImpl;

class CashIdActiveAuthKeysIT {
	
	private CashIdCredentialsService credentialsService;

	@BeforeEach
	void initService() {
		credentialsService = new CashIdCredentialsServiceImpl(new AuthKeysConf(Duration.ofMillis(200), 64));
	}

	@Test
	void testWhenMoreTimeThanExpireThenCredentialsRemoved() throws InterruptedException, AuthenticationException {
		credentialsService.nonce("address1");
		credentialsService.nonce("address2");
		TimeUnit.MILLISECONDS.sleep(100);
		credentialsService.nonce("address3");
		TimeUnit.MILLISECONDS.sleep(100);
		credentialsService.removeExpiredCredentials();
		assertEquals(credentialsService.size(), 1);
	}

}
