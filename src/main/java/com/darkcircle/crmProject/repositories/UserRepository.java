package com.darkcircle.crmProject.repositories;

import com.darkcircle.crmProject.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String userName);
    Optional<User> findByAssignment(String assignment);
}
