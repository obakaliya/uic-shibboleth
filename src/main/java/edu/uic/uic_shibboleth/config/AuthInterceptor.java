package edu.uic.uic_shibboleth.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import edu.uic.uic_shibboleth.model.User;
import edu.uic.uic_shibboleth.repository.UserRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final UserRepository userRepository;

    @Value("${app.auth.auto-create-user:false}")
    private boolean autoCreateUser;

    @Value("${app.auth.local-dev-mode:false}")
    private boolean localDevMode;

    public AuthInterceptor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String path = request.getRequestURI();

        // Allow access to public paths
        if (path.equals("/login")
                || path.equals("/local-login")
                || path.equals("/local-logout")
                || path.startsWith("/css")
                || path.startsWith("/js")
                || path.startsWith("/h2-console")
                || path.startsWith("/Shibboleth.sso")
                || path.startsWith("/error")) {
            return true;
        }

        User authUser = null;

        if (localDevMode) {
            // Local development mode - check session for local user
            HttpSession session = request.getSession(false);
            if (session != null) {
                authUser = (User) session.getAttribute("localUser");
            }

            if (authUser == null) {
                response.sendRedirect("/login");
                return false;
            }
        } else {
            // Production mode - use Shibboleth headers
            Map<String, Object> headers = extractUserFromHeaders(request);

            if (!headers.containsKey("uid")) {
                response.sendRedirect("/Shibboleth.sso/Login");
                return false;
            }

            String uid = (String) headers.get("uid");
            Optional<User> userOpt = userRepository.findById(uid);
            if (userOpt.isEmpty()) {
                if (autoCreateUser) {
                    User newUser = createUserFromHeaders(headers);
                    userRepository.save(newUser);
                    authUser = newUser;
                } else {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "User not authorized - account not found");
                    return false;
                }
            } else {
                authUser = userOpt.get();
                // Update existing user with latest header values
                updateUserFromHeaders(authUser, headers);
                userRepository.save(authUser);
            }
        }

        request.setAttribute("authUser", authUser);
        return true;
    }

    private User createUserFromHeaders(Map<String, Object> headers) {
        User user = new User();

        // Core Required Attributes
        user.setUid((String) headers.get("uid"));
        user.setDisplayName((String) headers.get("displayName"));
        user.setEduPersonPrimaryAffiliation((String) headers.get("eduPersonPrimaryAffiliation"));
        user.setEduPersonPrincipalName((String) headers.get("eduPersonPrincipalName"));
        user.setEduPersonScopedAffiliation((String) headers.get("eduPersonScopedAffiliation"));
        user.setGivenName((String) headers.get("givenName"));
        user.setMail((String) headers.get("mail"));
        user.setOrganizationName((String) headers.get("organizationName"));
        user.setSurname((String) headers.get("sn"));

        // Handle boolean iTrustSuppress
        String suppressStr = (String) headers.get("iTrustSuppress");
        if (suppressStr != null) {
            user.setITrustSuppress(Boolean.parseBoolean(suppressStr));
        }

        // Optional Attributes
        user.setITrustHomeDeptCode((String) headers.get("iTrustHomeDeptCode"));
        user.setITrustUIN((String) headers.get("iTrustUIN"));
        user.setOrganizationalUnit((String) headers.get("organizationalUnit"));
        user.setTitle((String) headers.get("title"));

        return user;
    }

    private void updateUserFromHeaders(User user, Map<String, Object> headers) {
        // Update all attributes with latest values from headers
        if (headers.containsKey("displayName")) {
            user.setDisplayName((String) headers.get("displayName"));
        }
        if (headers.containsKey("eduPersonPrimaryAffiliation")) {
            user.setEduPersonPrimaryAffiliation((String) headers.get("eduPersonPrimaryAffiliation"));
        }
        if (headers.containsKey("eduPersonPrincipalName")) {
            user.setEduPersonPrincipalName((String) headers.get("eduPersonPrincipalName"));
        }
        if (headers.containsKey("eduPersonScopedAffiliation")) {
            user.setEduPersonScopedAffiliation((String) headers.get("eduPersonScopedAffiliation"));
        }
        if (headers.containsKey("givenName")) {
            user.setGivenName((String) headers.get("givenName"));
        }
        if (headers.containsKey("mail")) {
            user.setMail((String) headers.get("mail"));
        }
        if (headers.containsKey("organizationName")) {
            user.setOrganizationName((String) headers.get("organizationName"));
        }
        if (headers.containsKey("sn")) {
            user.setSurname((String) headers.get("sn"));
        }
        if (headers.containsKey("iTrustSuppress")) {
            String suppressStr = (String) headers.get("iTrustSuppress");
            if (suppressStr != null) {
                user.setITrustSuppress(Boolean.parseBoolean(suppressStr));
            }
        }
        if (headers.containsKey("iTrustHomeDeptCode")) {
            user.setITrustHomeDeptCode((String) headers.get("iTrustHomeDeptCode"));
        }
        if (headers.containsKey("iTrustUIN")) {
            user.setITrustUIN((String) headers.get("iTrustUIN"));
        }
        if (headers.containsKey("organizationalUnit")) {
            user.setOrganizationalUnit((String) headers.get("organizationalUnit"));
        }
        if (headers.containsKey("title")) {
            user.setTitle((String) headers.get("title"));
        }
    }

    private Map<String, Object> extractUserFromHeaders(HttpServletRequest request) {
        Map<String, Object> user = new HashMap<>();

        // user attributes
        mapHeader(request, "displayName", user);
        mapHeader(request, "eduPersonPrimaryAffiliation", user);
        mapHeader(request, "eduPersonPrincipalName", user);
        mapHeader(request, "eduPersonScopedAffiliation", user);
        mapHeader(request, "givenName", user);
        mapHeader(request, "iTrustSuppress", user);
        mapHeader(request, "mail", user);
        mapHeader(request, "organizationName", user);
        mapHeader(request, "sn", user);
        mapHeader(request, "uid", user);
        mapHeader(request, "iTrustHomeDeptCode", user);
        mapHeader(request, "iTrustUIN", user);
        mapHeader(request, "organizationalUnit", user);
        mapHeader(request, "title", user);

        return user;
    }

    private void mapHeader(HttpServletRequest request, String headerName, Map<String, Object> user) {
        String value = request.getHeader(headerName);
        if (value != null && !value.isBlank()) {
            user.put(headerName, value);
        }
    }
}