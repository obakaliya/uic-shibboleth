package edu.uic.uic_shibboleth.controller;

import edu.uic.uic_shibboleth.annotation.AuthenticatedUser;
import edu.uic.uic_shibboleth.model.User;
import edu.uic.uic_shibboleth.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ApiController {

    private final UserService userService;

    public ApiController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/user")
    public Map<String, Object> getUser(@AuthenticatedUser User user) {
        Map<String, Object> response = new HashMap<>();

        if (user != null) {
            response.put("authenticated", true);
            response.put("user", user);
        } else {
            response.put("authenticated", false);
            response.put("reason", "no_user_found");
        }

        return response;
    }
}