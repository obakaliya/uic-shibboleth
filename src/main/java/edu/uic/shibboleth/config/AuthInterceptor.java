package edu.uic.shibboleth.config;

import edu.uic.shibboleth.exception.auth.UnauthenticatedException;
import edu.uic.shibboleth.exception.auth.UnauthorizedException;
import edu.uic.shibboleth.mapper.UserMapper;
import edu.uic.shibboleth.model.User;
import edu.uic.shibboleth.repository.UserRepository;
import edu.uic.shibboleth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final UserRepository userRepository;
    private final UserMapper headerUserMapper;
    private final UserService userService;

    @Value("${app.auth.auto-create-user:false}")
    private boolean autoCreateUser;

    @Value("${app.auth.local-dev-mode:false}")
    private boolean localDevMode;
    
    @Value("${app.auth.login-url:/Shibboleth.sso/Login}")
    private String LOGIN_URL;

    public AuthInterceptor(UserRepository userRepository, UserMapper headerUserMapper, UserService userService) {
        this.userRepository = userRepository;
        this.headerUserMapper = headerUserMapper;
        this.userService = userService; 
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String path = request.getRequestURI();

        if (isPublicPath(path)) {
            return true;
        }

        try {
            User authUser = localDevMode ? getLocalDevUser(request) : authenticateFromAttributes(request);
            request.setAttribute("authUser", authUser);
            return true;
        } catch (UnauthenticatedException | UnauthorizedException e) {
            response.sendRedirect(LOGIN_URL);
            return false;
        }
    }

    private boolean isPublicPath(String path) {
        return path.equals("/login")
                || path.equals("/local-login")
                || path.equals("/local-logout")
                || path.startsWith("/css")
                || path.startsWith("/js")
                || path.startsWith("/h2-console")
                || path.startsWith("/Shibboleth.sso")
                || path.startsWith("/error");
    }

    private User getLocalDevUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("localUser") == null) {
            throw new UnauthenticatedException("User not logged in (local dev mode).");
        }
        return (User) session.getAttribute("localUser");
    }

    private User authenticateFromAttributes(HttpServletRequest request) {
        Map<String, Object> attributes = headerUserMapper.extractAttributes(request);

        String uid = (String) attributes.get("uid");
        if (uid == null) {
            throw new UnauthenticatedException("Missing Shibboleth uid attribute.");
        }

        return userRepository.findById(uid)
                .map(user -> {
                    userService.updateUserFromAttributes(user, attributes);
                    userRepository.save(user);
                    return user;
                })
                .orElseGet(() -> {
                    if (!autoCreateUser) {
                        throw new UnauthorizedException("User not authorized - account not found");
                    }
                    User newUser = userService.createUserFromAttributes(attributes);
                    userRepository.save(newUser);
                    return newUser;
                });
    }
}
