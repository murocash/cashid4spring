package cash.muro.springsecurity.authentication.cashid.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CashIdStatusTest {

	@Test
	void testGetJson() {
		assertEquals(CashIdStatus.AUTHENTICATION_SUCCESSFUL.getJson(), "{\"status\": 0, \"message\": \"Authenticated\"}");
		assertEquals(CashIdStatus.RESPONSE_BROKEN.getJson(), "{\"status\": 200, \"message\": \"Could not parse CashId Response\"}");
	}

}
