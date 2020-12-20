package cash.muro.springsecurity.authentication.cashid;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CashIdResponseBody {

	private String request;
	private String address;
	private String signature;
	private boolean verified = false;
}
