package edu.uic.shibboleth.controller.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.uic.shibboleth.annotation.AuthenticatedUser;
import edu.uic.shibboleth.controller.dto.UserResponseDto;
import edu.uic.shibboleth.model.User;
import edu.uic.shibboleth.service.UserService;

@RestController("UserApiController")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/user")
    public UserResponseDto getUser(@AuthenticatedUser User user) {
        return user != null ? new UserResponseDto(true, user) : new UserResponseDto(false, null);
    }
}