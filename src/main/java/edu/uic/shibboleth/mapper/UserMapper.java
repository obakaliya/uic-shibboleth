package edu.uic.shibboleth.mapper;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserMapper {

    public Map<String, Object> extractAttributes(HttpServletRequest request) {
        Map<String, Object> attributes = new HashMap<>();
        putIfPresent(request, "displayname", "displayName", attributes);
        putIfPresent(request, "uid", "uid", attributes);
        putIfPresent(request, "mail", "mail", attributes);
        putIfPresent(request, "givenname", "givenName", attributes);
        putIfPresent(request, "sn", "sn", attributes);
        putIfPresent(request, "itrustuin", "iTrustUIN", attributes);
        putIfPresent(request, "itrustsuppress", "iTrustSuppress", attributes);
        putIfPresent(request, "title", "title", attributes);
        putIfPresent(request, "itrusthomedeptcode", "iTrustHomeDeptCode", attributes);
        putIfPresent(request, "primary-affiliation", "eduPersonPrimaryAffiliation", attributes);
        putIfPresent(request, "affiliation", "eduPersonScopedAffiliation", attributes);
        putIfPresent(request, "eppn", "eduPersonPrincipalName", attributes);
        putIfPresent(request, "o", "organizationName", attributes);
        putIfPresent(request, "ou", "organizationalUnit", attributes);
        return attributes;
    }

    private void putIfPresent(HttpServletRequest request,
                              String headerName,
                              String key,
                              Map<String, Object> attributes) {
        String value = request.getHeader(headerName);
        if (value != null && !value.isBlank()) {
            attributes.put(key, value);
        }
    }
}
