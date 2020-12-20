package cash.muro.springsecurity.authentication.cashid.model;

import org.springframework.lang.NonNull;

public enum CashIdStatus {

	AUTHENTICATION_SUCCESSFUL(0, "Authenticated"), RESPONSE_BROKEN(200, "Could not parse CashId Response"),
	RESPONSE_INVALID_SIGNATURE(233, "The Signature is not valid");

	private final static String CONFIRMATION_TEMPLATE = "{\"status\": %d, \"message\": \"%s\"}";
	@NonNull
	private final int code;
	@NonNull
	private final String message;

	CashIdStatus(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public String getJson() {
		return String.format(CONFIRMATION_TEMPLATE, code, message);
	}
}
