package cash.muro.springsecurity.authentication.cashid;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
public class CashIdCredentials {

	@NonNull
	private final String nonce;
	@Setter
	private CashIdResponseBody response;
	@NonNull
	private final LocalDateTime expirationTime;

	public CashIdCredentials(String nonce, LocalDateTime expirationTime) {
		this.nonce = nonce;
		this.expirationTime = expirationTime;
	}

}
