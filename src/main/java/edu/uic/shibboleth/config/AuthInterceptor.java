package edu.uic.shibboleth.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import edu.uic.shibboleth.model.User;
import edu.uic.shibboleth.repository.UserRepository;

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
            HttpSession session = request.getSession(false);
            if (session != null) {
                authUser = (User) session.getAttribute("localUser");
            }

            if (authUser == null) {
                response.sendRedirect("/login");
                return false;
            }
        } else {
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
                    response.sendError(HttpServletResponse.SC_FORBIDDEN,
                            "User not authorized - account not found");
                    return false;
                }
            } else {
                authUser = userOpt.get();
                updateUserFromHeaders(authUser, headers);
                userRepository.save(authUser);
            }
        }

        request.setAttribute("authUser", authUser);
        return true;
    }

    private User createUserFromHeaders(Map<String, Object> headers) {
        User user = new User();

        user.setUid((String) headers.get("uid"));
        user.setDisplayName((String) headers.get("displayName"));
        user.setEduPersonPrimaryAffiliation((String) headers.get("eduPersonPrimaryAffiliation"));
        user.setEduPersonPrincipalName((String) headers.get("eduPersonPrincipalName"));
        user.setEduPersonScopedAffiliation((String) headers.get("eduPersonScopedAffiliation"));
        user.setGivenName((String) headers.get("givenName"));
        user.setMail((String) headers.get("mail"));
        user.setOrganizationName((String) headers.get("organizationName"));
        user.setSurname((String) headers.get("sn"));

        String suppressStr = (String) headers.get("iTrustSuppress");
        if (suppressStr != null) {
            user.setITrustSuppress(Boolean.parseBoolean(suppressStr));
        }

        user.setITrustHomeDeptCode((String) headers.get("iTrustHomeDeptCode"));
        user.setITrustUIN((String) headers.get("iTrustUIN"));
        user.setOrganizationalUnit((String) headers.get("organizationalUnit"));
        user.setTitle((String) headers.get("title"));

        return user;
    }

    private void updateUserFromHeaders(User user, Map<String, Object> headers) {
        if (headers.containsKey("displayName"))
            user.setDisplayName((String) headers.get("displayName"));
        if (headers.containsKey("eduPersonPrimaryAffiliation"))
            user.setEduPersonPrimaryAffiliation((String) headers.get("eduPersonPrimaryAffiliation"));
        if (headers.containsKey("eduPersonPrincipalName"))
            user.setEduPersonPrincipalName((String) headers.get("eduPersonPrincipalName"));
        if (headers.containsKey("eduPersonScopedAffiliation"))
            user.setEduPersonScopedAffiliation((String) headers.get("eduPersonScopedAffiliation"));
        if (headers.containsKey("givenName"))
            user.setGivenName((String) headers.get("givenName"));
        if (headers.containsKey("mail"))
            user.setMail((String) headers.get("mail"));
        if (headers.containsKey("organizationName"))
            user.setOrganizationName((String) headers.get("organizationName"));
        if (headers.containsKey("sn"))
            user.setSurname((String) headers.get("sn"));
        if (headers.containsKey("iTrustSuppress")) {
            String suppressStr = (String) headers.get("iTrustSuppress");
            if (suppressStr != null)
                user.setITrustSuppress(Boolean.parseBoolean(suppressStr));
        }
        if (headers.containsKey("iTrustHomeDeptCode"))
            user.setITrustHomeDeptCode((String) headers.get("iTrustHomeDeptCode"));
        if (headers.containsKey("iTrustUIN"))
            user.setITrustUIN((String) headers.get("iTrustUIN"));
        if (headers.containsKey("organizationalUnit"))
            user.setOrganizationalUnit((String) headers.get("organizationalUnit"));
        if (headers.containsKey("title"))
            user.setTitle((String) headers.get("title"));
    }

    private Map<String, Object> extractUserFromHeaders(HttpServletRequest request) {
        Map<String, Object> user = new HashMap<>();

        mapHeader(request, "displayname", "displayName", user);
        mapHeader(request, "uid", "uid", user);
        mapHeader(request, "mail", "mail", user);
        mapHeader(request, "givenname", "givenName", user);
        mapHeader(request, "sn", "sn", user);
        mapHeader(request, "itrustuin", "iTrustUIN", user);
        mapHeader(request, "itrustsuppress", "iTrustSuppress", user);
        mapHeader(request, "title", "title", user);
        mapHeader(request, "itrusthomedeptcode", "iTrustHomeDeptCode", user);
        mapHeaderWithAlias(request, "primary-affiliation", "eduPersonPrimaryAffiliation", user);
        mapHeaderWithAlias(request, "affiliation", "eduPersonScopedAffiliation", user);
        mapHeaderWithAlias(request, "eppn", "eduPersonPrincipalName", user);
        mapHeaderWithAlias(request, "o", "organizationName", user);
        mapHeaderWithAlias(request, "ou", "organizationalUnit", user);

        return user;
    }

    private void mapHeader(HttpServletRequest request, String headerName, String javaKey, Map<String, Object> user) {
        String value = request.getHeader(headerName);
        if (value != null && !value.isBlank()) {
            user.put(javaKey, value);
        }
    }

    private void mapHeaderWithAlias(HttpServletRequest request, String headerName, String alias,
            Map<String, Object> user) {
        String value = request.getHeader(headerName);
        if (value != null && !value.isBlank()) {
            user.put(alias, value);
        }
    }
}
