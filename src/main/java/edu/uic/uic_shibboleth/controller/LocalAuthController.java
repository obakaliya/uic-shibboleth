package edu.uic.uic_shibboleth.controller;

import edu.uic.uic_shibboleth.model.User;
import edu.uic.uic_shibboleth.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

@Controller
@ConditionalOnProperty(name = "app.auth.local-dev-mode", havingValue = "true")
public class LocalAuthController {

    private final UserRepository userRepository;

    public LocalAuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        // Create dummy users if they don't exist
        createDummyUsersIfNeeded();
        return "local-login";
    }

    @PostMapping("/local-login")
    public String localLogin(@RequestParam String email, @RequestParam String password, HttpSession session) {
        // Check if password is correct (default: "password")
        if (!"password".equals(password)) {
            return "redirect:/login?error=invalid_password";
        }
        
        // Find user by email
        User user = userRepository.findByMail(email);
        if (user != null) {
            session.setAttribute("localUser", user);
            return "redirect:/user";
        }
        return "redirect:/login?error=user_not_found";
    }

    @PostMapping("/local-logout")
    public String localLogout(HttpSession session) {
        session.removeAttribute("localUser");
        return "redirect:/";
    }

    private void createDummyUsersIfNeeded() {
        if (userRepository.count() == 0) {
            List<User> dummyUsers = Arrays.asList(
                createDummyUser("dev001", "John Developer", "john.dev@uic.edu", "John", "Developer", "faculty", "Computer Science"),
                createDummyUser("test002", "Jane Tester", "jane.test@uic.edu", "Jane", "Tester", "staff", "Information Technology"),
                createDummyUser("admin003", "Admin User", "admin@uic.edu", "Admin", "User", "employee", "Administration"),
                createDummyUser("student004", "Student User", "student@uic.edu", "Student", "User", "student", "Engineering")
            );
            
            userRepository.saveAll(dummyUsers);
        }
    }

    private User createDummyUser(String uid, String displayName, String email, String givenName, String surname, String affiliation, String ou) {
        User user = new User();
        user.setUid(uid);
        user.setDisplayName(displayName);
        user.setMail(email);
        user.setGivenName(givenName);
        user.setSurname(surname);
        user.setEduPersonPrimaryAffiliation(affiliation);
        user.setEduPersonScopedAffiliation(affiliation + "@uic.edu");
        user.setEduPersonPrincipalName(email);
        user.setOrganizationName("University of Illinois Chicago");
        user.setOrganizationalUnit(ou);
        user.setITrustUIN("UIN" + uid);
        user.setTitle("Local Development User");
        user.setITrustSuppress(false);
        return user;
    }
}