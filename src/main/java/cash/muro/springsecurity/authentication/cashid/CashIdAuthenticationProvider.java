package cash.muro.springsecurity.authentication.cashid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import cash.muro.springsecurity.authentication.cashid.service.CashIdCredentialsService;
import cash.muro.springsecurity.authorization.AuthoritiesService;

@Component
public class CashIdAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private CashIdCredentialsService credentialsService;
	
	private AuthoritiesService authoritiesService;

	public CashIdAuthenticationProvider(AuthoritiesService authoritiesService) {
		this.authoritiesService = authoritiesService;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		CashIdAuthenticationToken token = (CashIdAuthenticationToken) authentication;
		if (token == null || token.getPrincipal() == null) {
			return null;
		}
		CashIdAuthenticationDetails details = token.getDetails();		
		if( details == null	|| !details.validate()) {
			return null;
		}
		String address = (String)token.getPrincipal();
		boolean validation = credentialsService.validateCredentials(address);
		credentialsService.removeCredentials(address);
		if (!validation) {
			return null;
		}		
		Authentication result = new CashIdAuthenticationToken(address, authoritiesService.authorities(address));
		return result;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(CashIdAuthenticationToken.class);
	}

}
