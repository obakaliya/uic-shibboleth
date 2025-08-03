package edu.uic.shibboleth.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import edu.uic.shibboleth.annotation.AuthenticatedUser;
import edu.uic.shibboleth.model.User;

@Controller("UserWebController")
public class UserController {

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