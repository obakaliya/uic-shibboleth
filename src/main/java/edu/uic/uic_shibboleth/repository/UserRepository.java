package edu.uic.uic_shibboleth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.uic.uic_shibboleth.model.User;

public interface UserRepository extends JpaRepository<User, String> {
    User findByMail(String mail);
}
