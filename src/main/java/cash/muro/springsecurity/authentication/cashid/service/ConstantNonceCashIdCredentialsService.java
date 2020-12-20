package cash.muro.springsecurity.authentication.cashid.service;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import cash.muro.springsecurity.authentication.cashid.CashIdCredentials;
import cash.muro.springsecurity.authentication.cashid.conf.AuthKeysConf;

/**
 * Use this CashIdCredentialsService implementation ONLY for TESTING PURPOSES.
 * It does not provide a random nonce, but a constant one which has the same
 * value as the address.
 */

public class ConstantNonceCashIdCredentialsService extends CashIdCredentialsServiceImpl {


	public ConstantNonceCashIdCredentialsService(AuthKeysConf conf) {
		super(conf);
	}


	public String nonce(String address) throws UsernameNotFoundException {
		if (address == null) {
			throw new UsernameNotFoundException("Address can not be null");
		}
		CashIdCredentials credentials;
		Set<String> currentNonces = keySet(n -> n.getNonce());
		synchronized (currentNonces) {
			credentials = new CashIdCredentials(address, LocalDateTime.now().plus(authKeysConf.getTimeToExpire()));
		}
		activeCredentials.put(address, credentials);
		return credentials.getNonce();
	}

}
