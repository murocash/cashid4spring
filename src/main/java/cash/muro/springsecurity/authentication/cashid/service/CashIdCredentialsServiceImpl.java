package cash.muro.springsecurity.authentication.cashid.service;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.emiperez.repeson.client.JsonRpcException;

import cash.muro.bch.model.BchAddress;
import cash.muro.bch.model.BchException;
import cash.muro.bch.model.BchValidatedAddress;
import cash.muro.bch.rpc.client.BchnRpcClient;
import cash.muro.springsecurity.authentication.cashid.CashIdCredentials;
import cash.muro.springsecurity.authentication.cashid.CashIdResponseBody;
import cash.muro.springsecurity.authentication.cashid.conf.AuthKeysConf;

@Service
@Qualifier("credentialsService")
public class CashIdCredentialsServiceImpl implements CashIdCredentialsService {

	private final static Random RANDOM = new Random();

	protected final AuthKeysConf authKeysConf;

	protected Map<String, CashIdCredentials> activeCredentials = new HashMap<>();

	@Autowired
	 BchnRpcClient bchClient;

	public CashIdCredentialsServiceImpl(AuthKeysConf conf) {
		this.authKeysConf = conf;
	}

	public int size() {
		return activeCredentials.size();
	}

	public int getRetryAfter() {
		return (int) authKeysConf.getTimeToExpire().getSeconds() * 2;
	}	
	
	@Override
	public BchValidatedAddress validateAddress(String address) throws BchException {
		if (!BchAddress.isCashAddress(address)) {
			throw new BchException("Incorrect address format. Make sure that it includes the prefix");
		}
		try {
			return bchClient.validateAddress(new BchAddress(address));
		} catch (IOException | InterruptedException | JsonRpcException e) {
			throw new BchException("Error while validating address", e);
		}
	}

	public String nonce(String address) throws UsernameNotFoundException {
		if (address == null) {
			throw new UsernameNotFoundException("Address can not be null");
		}
		CashIdCredentials credentials;
		Set<String> currentNonces = keySet(n -> n.getNonce());
		synchronized (currentNonces) {
			credentials = new CashIdCredentials(newKey(currentNonces), LocalDateTime.now().plus(authKeysConf.getTimeToExpire()));
		}
		activeCredentials.put(address, credentials);
		return credentials.getNonce();
	}

	public CashIdCredentials getCredentials(String address) {
		return activeCredentials.get(address);
	}

	public boolean existCredentials(String address) {
		return activeCredentials.containsKey(address);
	}

	public boolean validateCredentials(String address) {
		CashIdResponseBody response = activeCredentials.get(address).getResponse();
		boolean result = false;
		try {
			result = bchClient.verifyMessage(new BchAddress(address), response.getSignature(), response.getRequest());
		} catch (IOException | InterruptedException | JsonRpcException e) {
			throw new BadCredentialsException("Error while validating signature", e);
		}
		return result;
	}

	public CashIdCredentials removeCredentials(String address) {
		return activeCredentials.remove(address);
	}

	public boolean removeExpiredCredentials() {
		return activeCredentials.values().removeIf(n -> n.getExpirationTime().isBefore(LocalDateTime.now()));
	}

	private String newKey(Set<String> currentKeys) throws AuthenticationServiceException {
		int tries = 0;
		String keyToTry = new BigInteger(authKeysConf.getBits(), RANDOM).toString();
		while (currentKeys.contains(keyToTry) && tries < 3) {
			tries++;
			keyToTry = new BigInteger(authKeysConf.getBits(), RANDOM).toString();
		}
		if (tries == 3) {
			throw new AuthenticationServiceException("Too many collisions when creating unique key");
		}
		return new BigInteger(authKeysConf.getBits(), RANDOM).toString();
	}

	protected Set<String> keySet(Function<CashIdCredentials, String> keyMapper) {
		return Collections.synchronizedSet(activeCredentials.values().stream().map(keyMapper).collect(Collectors.toSet()));
	}

}
