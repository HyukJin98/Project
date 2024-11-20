package edu.du.samplep.controller;

import edu.du.samplep.service.RegisterRequest;
import edu.du.samplep.service.UserService;
import groovyjarjarpicocli.CommandLine;
import javassist.bytecode.DuplicateMemberException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Controller
public class RegisterController {

	@Autowired
	private UserService userService;


	@RequestMapping("/register/step1")
	public String handleStep1() {
		return "register/step1";
	}

	@PostMapping("/register/step2")
	public String handleStep2(
			@RequestParam(value = "agree", defaultValue = "false") Boolean agree,
			Model model) {
		if (!agree) {
			return "register/step1";
		}
		model.addAttribute("registerRequest", new RegisterRequest());
		return "register/step2";
	}

	@GetMapping("/register/step2")
	public String handleStep2Get() {
		return "redirect:/register/step1";
	}

//	@PostMapping("/register/step3")
//	public String handleStep3(RegisterRequest regReq) {
//		try {
//			memberRegisterService.regist(regReq);
//			return "register/step3";
//		} catch (DuplicateMemberException ex) {
//			return "register/step2";
//		}
//	}
@PostMapping("/register/step3")
public String handleStep3(@Valid RegisterRequest regReq, Errors errors) {
//	new RegisterRequestValidator().validate(regReq, errors);
	if (errors.hasErrors())
		return "register/step2";

	try {
		userService.register(regReq);
		return "register/step3";
	} catch (DuplicateMemberException ex) {
			errors.rejectValue("email", "duplicate");
//		errors.reject("notMatchingPassword");
		return "register/step2";
	} catch (CommandLine.DuplicateNameException ex1){

		errors.rejectValue("name", "duplicate");
		return "register/step2";
	}
}

	@InitBinder
	protected void InitBinder(WebDataBinder binder) {
		binder.addValidators(new RegisterRequestValidator());
	}

}
