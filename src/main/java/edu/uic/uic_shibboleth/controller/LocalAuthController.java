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
        // create dummy users if they don't exist
        createDummyUsersIfNeeded();
        return "local-login";
    }

    @PostMapping("/local-login")
    public String localLogin(@RequestParam String email, @RequestParam String password, HttpSession session) {
        // check if password is correct (default: "password")
        if (!"password".equals(password)) {
            return "redirect:/login?error=invalid_password";
        }
        
        // Find user by email
        User user = userRepository.findByMail(email);
        if (user != null) {
            session.setAttribute("localUser", user);
            return "redirect:/";
        }
        return "redirect:/login?error=user_not_found";
    }

    @PostMapping("/local-logout")
    public String localLogout(HttpSession session) {
        session.removeAttribute("localUser");
        return "redirect:/login";
    }

    private void createDummyUsersIfNeeded() {
        if (userRepository.count() == 0) {
            List<User> dummyUsers = Arrays.asList(
                createDummyUser(
                    "jdev001", 
                    "John Developer", 
                    "john.dev@uic.edu", 
                    "John", 
                    "Developer", 
                    "faculty", 
                    "Computer Science",
                    "Professor",
                    "CS-101-001",
                    "123456789"
                ),
                createDummyUser(
                    "jtest002", 
                    "Jane Tester", 
                    "jane.test@uic.edu", 
                    "Jane", 
                    "Tester", 
                    "staff", 
                    "Information Technology",
                    "IT Specialist",
                    "IT-200-002",
                    "234567890"
                ),
                createDummyUser(
                    "admin003", 
                    "Admin User", 
                    "admin@uic.edu", 
                    "Admin", 
                    "User", 
                    "employee", 
                    "Administration",
                    "System Administrator",
                    "AD-300-003",
                    "345678901"
                ),
                createDummyUser(
                    "stud004", 
                    "Student User", 
                    "student@uic.edu", 
                    "Student", 
                    "User", 
                    "student", 
                    "Engineering",
                    "Graduate Student",
                    "EN-400-004",
                    "456789012"
                )
            );
            
            userRepository.saveAll(dummyUsers);
        }
    }

    private User createDummyUser(String uid, String displayName, String email, String givenName, 
                                String surname, String affiliation, String ou, String title, 
                                String deptCode, String uin) {
        User user = new User();

        // user attributes
        user.setUid(uid);
        user.setDisplayName(displayName);
        user.setMail(email);
        user.setGivenName(givenName);
        user.setSurname(surname);
        user.setEduPersonPrimaryAffiliation(affiliation);
        user.setEduPersonScopedAffiliation(affiliation + "@uic.edu");
        user.setEduPersonPrincipalName(email);
        user.setOrganizationName("University of Illinois Chicago");
        user.setITrustSuppress(false);
        user.setOrganizationalUnit(ou);
        user.setTitle(title);
        user.setITrustHomeDeptCode(deptCode);
        user.setITrustUIN(uin);
        
        return user;
    }
}