package edu.uic.shibboleth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.uic.shibboleth.model.User;

public interface UserRepository extends JpaRepository<User, String> {
    User findByMail(String mail);
}
