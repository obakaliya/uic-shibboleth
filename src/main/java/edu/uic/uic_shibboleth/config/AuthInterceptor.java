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
        if (path.equals("/")
                || path.equals("/login")
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
                    User newUser = new User();
                    newUser.setUid(uid);
                    newUser.setDisplayName((String) headers.get("displayname"));
                    newUser.setEduPersonPrimaryAffiliation((String) headers.get("primary-affiliation"));
                    newUser.setEduPersonPrincipalName((String) headers.get("eppn"));
                    newUser.setEduPersonScopedAffiliation((String) headers.get("affiliation"));
                    newUser.setGivenName((String) headers.get("givenname"));
                    String suppressStr = (String) headers.get("itrustsuppress");
                    if (suppressStr != null) {
                        newUser.setITrustSuppress(Boolean.parseBoolean(suppressStr));
                    }
                    newUser.setMail((String) headers.get("mail"));
                    newUser.setOrganizationName((String) headers.get("o"));
                    newUser.setSurname((String) headers.get("sn"));
                    newUser.setITrustUIN((String) headers.get("itrustuin"));
                    newUser.setOrganizationalUnit((String) headers.get("ou"));
                    newUser.setTitle((String) headers.get("title"));

                    userRepository.save(newUser);
                    authUser = newUser;
                } else {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "User not authorized - account not found");
                    return false;
                }
            } else {
                authUser = userOpt.get();
            }
        }

        request.setAttribute("authUser", authUser);
        return true;
    }

    private Map<String, Object> extractUserFromHeaders(HttpServletRequest request) {
        Map<String, Object> user = new HashMap<>();

        mapHeader(request, "displayname", user);
        mapHeader(request, "primary-affiliation", user);
        mapHeader(request, "eppn", user);
        mapHeader(request, "affiliation", user);
        mapHeader(request, "givenname", user);
        mapHeader(request, "itrustsuppress", user);
        mapHeader(request, "mail", user);
        mapHeader(request, "o", user);
        mapHeader(request, "sn", user);
        mapHeader(request, "itrustuin", user);
        mapHeader(request, "ou", user);
        mapHeader(request, "title", user);
        mapHeader(request, "uid", user);

        return user;
    }

    private void mapHeader(HttpServletRequest request, String headerName, Map<String, Object> user) {
        String value = request.getHeader(headerName);
        if (value != null && !value.isBlank()) {
            user.put(headerName, value);
        }
    }
}