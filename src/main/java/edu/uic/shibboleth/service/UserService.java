package edu.uic.shibboleth.service;

import java.util.Map;
import org.springframework.stereotype.Service;
import edu.uic.shibboleth.model.User;

@Service
public class UserService {
    /**
     * Create a User object from Shibboleth attributes.
     */
    public User createUserFromAttributes(Map<String, Object> attributes) {
        User user = new User();

        user.setUid((String) attributes.get("uid"));
        user.setDisplayName((String) attributes.get("displayName"));
        user.setEduPersonPrimaryAffiliation((String) attributes.get("eduPersonPrimaryAffiliation"));
        user.setEduPersonPrincipalName((String) attributes.get("eduPersonPrincipalName"));
        user.setEduPersonScopedAffiliation((String) attributes.get("eduPersonScopedAffiliation"));
        user.setGivenName((String) attributes.get("givenName"));
        user.setMail((String) attributes.get("mail"));
        user.setOrganizationName((String) attributes.get("organizationName"));
        user.setSurname((String) attributes.get("sn"));

        String suppressStr = (String) attributes.get("iTrustSuppress");
        if (suppressStr != null) {
            user.setITrustSuppress(Boolean.parseBoolean(suppressStr));
        }

        user.setITrustHomeDeptCode((String) attributes.get("iTrustHomeDeptCode"));
        user.setITrustUIN((String) attributes.get("iTrustUIN"));
        user.setOrganizationalUnit((String) attributes.get("organizationalUnit"));
        user.setTitle((String) attributes.get("title"));

        return user;
    }

    /**
     * Update an existing User object from Shibboleth attributes.
     */
    public void updateUserFromAttributes(User user, Map<String, Object> attributes) {
        if (attributes.containsKey("displayName")) {
            user.setDisplayName((String) attributes.get("displayName"));
        }
        if (attributes.containsKey("eduPersonPrimaryAffiliation")) {
            user.setEduPersonPrimaryAffiliation((String) attributes.get("eduPersonPrimaryAffiliation"));
        }
        if (attributes.containsKey("eduPersonPrincipalName")) {
            user.setEduPersonPrincipalName((String) attributes.get("eduPersonPrincipalName"));
        }
        if (attributes.containsKey("eduPersonScopedAffiliation")) {
            user.setEduPersonScopedAffiliation((String) attributes.get("eduPersonScopedAffiliation"));
        }
        if (attributes.containsKey("givenName")) {
            user.setGivenName((String) attributes.get("givenName"));
        }
        if (attributes.containsKey("mail")) {
            user.setMail((String) attributes.get("mail"));
        }
        if (attributes.containsKey("organizationName")) {
            user.setOrganizationName((String) attributes.get("organizationName"));
        }
        if (attributes.containsKey("sn")) {
            user.setSurname((String) attributes.get("sn"));
        }
        if (attributes.containsKey("iTrustSuppress")) {
            String suppressStr = (String) attributes.get("iTrustSuppress");
            if (suppressStr != null) {
                user.setITrustSuppress(Boolean.parseBoolean(suppressStr));
            }
        }
        if (attributes.containsKey("iTrustHomeDeptCode")) {
            user.setITrustHomeDeptCode((String) attributes.get("iTrustHomeDeptCode"));
        }
        if (attributes.containsKey("iTrustUIN")) {
            user.setITrustUIN((String) attributes.get("iTrustUIN"));
        }
        if (attributes.containsKey("organizationalUnit")) {
            user.setOrganizationalUnit((String) attributes.get("organizationalUnit"));
        }
        if (attributes.containsKey("title")) {
            user.setTitle((String) attributes.get("title"));
        }
    }
}
