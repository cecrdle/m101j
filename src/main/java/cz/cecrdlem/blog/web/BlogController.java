package cz.cecrdlem.blog.web;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cz.cecrdlem.blog.BlogService;
import cz.cecrdlem.blog.to.User;
import cz.cecrdlem.blog.web.form.LoginForm;
import cz.cecrdlem.blog.web.form.RegisterForm;

/**
 * Handles requests for the application home page.
 */
@Controller
public class BlogController {
	@Autowired
	BlogService blogService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Model model, @CookieValue("session") String sessionId) {
		String username = blogService.findUserNameBySessionId(sessionId);
		return "blog_template";
	}

	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public String doSignup(RegisterForm registerForm, Model model,HttpServletResponse response) {
		model.addAttribute("userName", registerForm.getUsername());
		model.addAttribute("email", registerForm.getEmail());
		if (validateSignup(registerForm, model)) {
			// good user
			System.out.println("Signup: Creating user with: " + registerForm.getUsername() + " " + registerForm.getPassword());
			User user = new User(registerForm.getUsername(), registerForm.getPassword(), registerForm.getEmail());
			if (!blogService.addUser(user)) {
				// duplicate user
				model.addAttribute("username_error", "Username already in use, Please choose another");
				return "signup";
			} else {
				String sessionID = blogService.startSession(user.getUsername());
				System.out.println("Session ID is" + sessionID);
				response.addCookie(new Cookie("session", sessionID));
				return "redirect:welcome";
			}
		} else {
			// bad signup
			System.out.println("User Registration did not validate");
			return "signup";
		}

	}

	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public String signup(Model model) {
		// initialize values for the form.
		model.addAttribute("username", "");
		model.addAttribute("password", "");
		model.addAttribute("email", "");
		model.addAttribute("password_error", "");
		model.addAttribute("username_error", "");
		model.addAttribute("email_error", "");
		model.addAttribute("verify_error", "");
		return "signup";
	}

	@RequestMapping(value = "/welcome", method = RequestMethod.GET)
	public String welcome(Model model, @CookieValue("session") String sessionId) {
		String username = blogService.findUserNameBySessionId(sessionId);
		if (username == null) {
			System.out.println("welcome() can't identify the user, redirecting to signup");
			return "redirect:signup";
		} else {
			model.addAttribute("username", username);
			return "welcome";
		}
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(Model model) {
		model.addAttribute("username", "");
		model.addAttribute("login_error", "");
		return "login";
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String doLogin(LoginForm loginForm, Model model, HttpServletResponse response) {
		System.out.println("Login: User submitted: " + loginForm.getUsername() + "  " + loginForm.getPassword());
		User user = blogService.validateLogin(loginForm);
		if (user != null) {
			// valid user, let's log them in
			String sessionID = blogService.startSession(user.getUsername());
			if (sessionID == null) {
				return "redirect:internal_error";
			} else {
				// set the cookie for the user's browser
				response.addCookie(new Cookie("session", sessionID));
				return "redirect:welcome";
			}
		} else {
			model.addAttribute("username",loginForm.getUsername());
			model.addAttribute("password", "");
			model.addAttribute("login_error", "Invalid Login");
			return "login";
		}
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(Model model, @CookieValue("session") String sessionID) {
		if (sessionID == null) {
			// no session to end
			return "redirect:login";
		} else {
			// deletes from session table
			blogService.endSession(sessionID);
			return "redirect:login";
		}
	}

	@RequestMapping(value = "/internal_error", method = RequestMethod.GET)
	public String internalError(Model model) {
		model.addAttribute("error", "System has encountered an error.");
		return "errorTemplate";
	}

	// validates that the registration form has been filled out right and
	// username conforms
	public boolean validateSignup(RegisterForm registerForm, Model model) {
		String USER_RE = "^[a-zA-Z0-9_-]{3,20}$";
		String PASS_RE = "^.{3,20}$";
		String EMAIL_RE = "^[\\S]+@[\\S]+\\.[\\S]+$";

		model.addAttribute("username_error", "");
		model.addAttribute("password_error", "");
		model.addAttribute("verify_error", "");
		model.addAttribute("email_error", "");

		if (!registerForm.getUsername().matches(USER_RE)) {
			model.addAttribute("username_error", "invalid username. try just letters and numbers");
			return false;
		}

		if (!registerForm.getUsername().matches(PASS_RE)) {
			model.addAttribute("password_error", "invalid password.");
			return false;
		}

		if (!registerForm.getPassword().equals(registerForm.getVerify())) {
			model.addAttribute("verify_error", "password must match");
			return false;
		}

		if (!registerForm.getEmail().equals("")) {
			if (!registerForm.getEmail().matches(EMAIL_RE)) {
				model.addAttribute("email_error", "Invalid Email Address");
				return false;
			}
		}

		return true;
	}

}
