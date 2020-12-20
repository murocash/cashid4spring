package cash.muro.springsecurity.authentication.cashid.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.annotation.SessionScope;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import cash.muro.bch.model.BchException;
import cash.muro.bch.model.BchValidatedAddress;
import cash.muro.springsecurity.authentication.cashid.CashIdResponseBody;
import cash.muro.springsecurity.authentication.cashid.model.AddressNonce;
import cash.muro.springsecurity.authentication.cashid.model.CashIdStatus;
import cash.muro.springsecurity.authentication.cashid.service.CashIdCredentialsService;

@Controller
public class CashIdController {

	@Autowired
	private CashIdCredentialsService credentialsService;

	@RequestMapping(path = "/requestnonce", produces = MediaType.TEXT_PLAIN_VALUE, method = RequestMethod.POST)
	@SessionScope
	@ResponseBody
	@ExceptionHandler()
	public String getNonce(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		BchValidatedAddress vAddress;
		HttpStatus status = HttpStatus.BAD_REQUEST;
		String address = request.getParameter("address");
		try {
			vAddress = credentialsService.validateAddress(address);
		}
		catch (BchException e) {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			return "Could not validate address";
		}
		if (vAddress != null && vAddress.isIsvalid()) {
			if (credentialsService.existCredentials(address)) {
				status = HttpStatus.TOO_MANY_REQUESTS;
				int retryAfter = credentialsService.getRetryAfter();
				response.setStatus(status.value());
				response.setHeader(HttpHeaders.RETRY_AFTER, "" + retryAfter);
				return "Too Many Requests. Retry after " + retryAfter + " seconds ";
			}
			AddressNonce sessionAddressNonce = new AddressNonce(address, credentialsService.nonce(address));
			session.setAttribute("cashid", sessionAddressNonce);
			return sessionAddressNonce.getNonce();
		}
		response.setStatus(status.value());
		return "Invalid Address";
	}

	@CrossOrigin
	@RequestMapping(path = "/cashid", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
	@ResponseBody
	public CashIdStatus cashidAuth(@RequestBody String jsonString, HttpServletResponse response) {
		HttpStatus status = HttpStatus.UNAUTHORIZED;
		ObjectReader jsonParser = new ObjectMapper().reader();
		CashIdResponseBody responseBody = null;
		try {
			responseBody = jsonParser.readValue(jsonString, CashIdResponseBody.class);
		} catch (IOException e) {
			status = HttpStatus.BAD_REQUEST;
			response.setStatus(status.value());
			return CashIdStatus.RESPONSE_BROKEN;
		}
		credentialsService.getCredentials(responseBody.getAddress()).setResponse(responseBody);
		if (credentialsService.validateCredentials(responseBody.getAddress())) {
			return CashIdStatus.AUTHENTICATION_SUCCESSFUL;
		}
		credentialsService.removeCredentials(responseBody.getAddress());
		response.setStatus(status.value());
		return CashIdStatus.RESPONSE_INVALID_SIGNATURE;
	}

	@GetMapping("/testnet-signature-form")
	public String testnetSignatureForm() {
		return "testnet-signature-form";
	}

}
