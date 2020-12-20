package cash.muro.springsecurity.authorization.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController {

	@RequestMapping("/403")
	public String error() {		
		return "errors/403";
	}

}
