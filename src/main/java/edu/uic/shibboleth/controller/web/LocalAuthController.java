package edu.uic.shibboleth.controller.web;

import edu.uic.shibboleth.model.User;
import edu.uic.shibboleth.repository.UserRepository;
import edu.uic.shibboleth.service.LocalDummyUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@ConditionalOnProperty(name = "app.auth.local-dev-mode", havingValue = "true")
public class LocalAuthController {

    private final UserRepository userRepository;
    private final LocalDummyUserService dummyUserService;

    public LocalAuthController(UserRepository userRepository, LocalDummyUserService dummyUserService) {
        this.userRepository = userRepository;
        this.dummyUserService = dummyUserService;
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        dummyUserService.initializeDummyUsersIfNeeded();
        return "auth/local-login";
    }

    @PostMapping("/local-login")
    public String localLogin(@RequestParam String email,
                             @RequestParam String password,
                             HttpSession session) {

        if (!"password".equals(password)) {
            return "redirect:/login?error=invalid_password";
        }

        User user = userRepository.findByMail(email);
        if (user == null) {
            return "redirect:/login?error=user_not_found";
        }

        session.setAttribute("localUser", user);
        return "redirect:/";
    }

    @PostMapping("/local-logout")
    public String localLogout(HttpSession session) {
        session.removeAttribute("localUser");
        return "redirect:/login";
    }
}
