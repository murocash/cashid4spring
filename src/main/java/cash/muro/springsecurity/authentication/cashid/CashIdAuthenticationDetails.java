package cash.muro.springsecurity.authentication.cashid;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

import cash.muro.springsecurity.authentication.cashid.model.AddressNonce;

public class CashIdAuthenticationDetails extends WebAuthenticationDetails {

	private AddressNonce sessionDetails;
	private AddressNonce requestDetails;

	public CashIdAuthenticationDetails(HttpServletRequest request) {
		super(request);
		sessionDetails = (AddressNonce) request.getSession().getAttribute("cashid");
		requestDetails = new AddressNonce(request.getParameter("address"), request.getParameter("nonce"));
	}

	public boolean validate() {
		return sessionDetails.equals(requestDetails);
	}

}
