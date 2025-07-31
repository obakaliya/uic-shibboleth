package edu.uic.uic_shibboleth.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import edu.uic.uic_shibboleth.model.User;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ApiController {

    @GetMapping("/api/user")
    public Map<String, Object> getUser(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        User authUser = (User) request.getAttribute("authUser");

        if (authUser != null) {
            response.put("authenticated", true);
            response.put("user", authUser);
        } else {
            response.put("authenticated", false);
            response.put("reason", "unaccounted");
        }

        return response;
    }
}
