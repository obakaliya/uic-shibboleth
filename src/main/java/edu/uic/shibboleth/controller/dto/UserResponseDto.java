package edu.uic.shibboleth.controller.dto;

import edu.uic.shibboleth.model.User;

public class UserResponseDto {
    private boolean authenticated;
    private User user;

    public UserResponseDto(boolean authenticated, User user) {
        this.authenticated = authenticated;
        this.user = user;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public User getUser() {
        return user;
    }

}
