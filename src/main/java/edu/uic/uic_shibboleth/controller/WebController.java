package edu.uic.uic_shibboleth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import edu.uic.uic_shibboleth.annotation.AuthenticatedUser;
import edu.uic.uic_shibboleth.model.User;

@Controller
public class WebController {

    @GetMapping("/")
    public String home(@AuthenticatedUser User user, Model model) {
        model.addAttribute("authenticated", user != null);
        if (user != null) {
            model.addAttribute("user", user);
        }
        return "index";
    }

    @GetMapping("/user")
    public String userPage() {
        return "user";
    }
}