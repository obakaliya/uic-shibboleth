package edu.uic.shibboleth.service;

import edu.uic.shibboleth.model.User;
import edu.uic.shibboleth.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocalDummyUserService {

    private final UserRepository userRepository;

    public LocalDummyUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Initializes dummy users in local dev mode if the repository is empty.
     */
    public void initializeDummyUsersIfNeeded() {
        if (userRepository.count() > 0) {
            return;
        }

        List<User> dummyUsers = List.of(
            createDummyUser("jdev001", "John Developer", "john.dev@uic.edu",
                    "John", "Developer", "faculty", "Computer Science",
                    "Professor", "CS-101-001", "123456789"),
            createDummyUser("jtest002", "Jane Tester", "jane.test@uic.edu",
                    "Jane", "Tester", "staff", "Information Technology",
                    "IT Specialist", "IT-200-002", "234567890"),
            createDummyUser("admin003", "Admin User", "admin@uic.edu",
                    "Admin", "User", "employee", "Administration",
                    "System Administrator", "AD-300-003", "345678901"),
            createDummyUser("stud004", "Student User", "student@uic.edu",
                    "Student", "User", "student", "Engineering",
                    "Graduate Student", "EN-400-004", "456789012")
        );

        userRepository.saveAll(dummyUsers);
    }

    private User createDummyUser(String uid, String displayName, String email,
                                 String givenName, String surname,
                                 String affiliation, String ou, String title,
                                 String deptCode, String uin) {
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
        user.setITrustSuppress(false);
        user.setOrganizationalUnit(ou);
        user.setTitle(title);
        user.setITrustHomeDeptCode(deptCode);
        user.setITrustUIN(uin);
        return user;
    }
}
