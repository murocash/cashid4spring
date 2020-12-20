package cash.muro.springsecurity.authentication.cashid;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import cash.muro.springsecurity.authentication.cashid.service.CashIdCredentialsService;
import cash.muro.springsecurity.authentication.cashid.service.CashIdCredentialsServiceImpl;

public class CashIdAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	public CashIdAuthenticationFilter(AuthenticationManager authMgr, SimpleUrlAuthenticationSuccessHandler successHandler) {
		super(new AntPathRequestMatcher("/cashidauth", "POST"));
		setAuthenticationManager(authMgr);
		setAuthenticationSuccessHandler(successHandler);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {

		CashIdCredentials credentials = getCredentials(request);
		if (credentials == null) {
			return null;
		}
		Authentication auth = new CashIdAuthenticationToken(credentials.getResponse().getAddress(), credentials, new CashIdAuthenticationDetails(request));
		AuthenticationManager authMgr = this.getAuthenticationManager();
		return authMgr.authenticate(auth);
	}

	private CashIdCredentials getCredentials(HttpServletRequest request) {
		ServletContext servletContext = request.getServletContext();
		WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
		CashIdCredentialsService service = webApplicationContext.getBean(CashIdCredentialsServiceImpl.class);
		return service.getCredentials(request.getParameter("address"));
	}

}
