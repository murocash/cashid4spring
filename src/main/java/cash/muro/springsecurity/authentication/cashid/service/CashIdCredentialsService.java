package cash.muro.springsecurity.authentication.cashid.service;

import cash.muro.bch.model.BchException;
import cash.muro.bch.model.BchValidatedAddress;
import cash.muro.springsecurity.authentication.cashid.CashIdCredentials;

public interface CashIdCredentialsService {

	/**
	 * 
	 * @return number of addresses that have active credentials
	 */
	int size();

	/**
	 * 
	 * @return seconds to wait until next request
	 */
	int getRetryAfter();
	
	BchValidatedAddress validateAddress(String address) throws BchException;

	String nonce(String address);

	CashIdCredentials getCredentials(String address);

	boolean existCredentials(String address);

	boolean removeExpiredCredentials();

	boolean validateCredentials(String address);

	CashIdCredentials removeCredentials(String address);

}
