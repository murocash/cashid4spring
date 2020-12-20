package cash.muro.springsecurity.authentication.cashid;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class CashIdAuthenticationToken extends AbstractAuthenticationToken {

	private final Object principal;
	private CashIdCredentials credentials;

	public CashIdAuthenticationToken(Object principal, CashIdCredentials credentials, CashIdAuthenticationDetails details) {
		super(null);
		this.principal = principal;
		this.credentials = credentials;
		setDetails(details);
		setAuthenticated(false);
	}

	public CashIdAuthenticationToken(Object principal, Collection<GrantedAuthority> authorities) {
		super(authorities);
		this.principal = principal;
		super.setAuthenticated(true);
	}

	@Override
	public CashIdCredentials getCredentials() {
		return credentials;
	}

	@Override
	public Object getPrincipal() {
		return principal;
	}
	
	public CashIdAuthenticationDetails getDetails() {
		return (CashIdAuthenticationDetails)super.getDetails();
	}

	@Override
	public void eraseCredentials() {
		super.eraseCredentials();
		credentials = null;
	}

}
