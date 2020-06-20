package com.darkcircle.crmProject.repositories;

import com.darkcircle.crmProject.enums.Roles;
import com.darkcircle.crmProject.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String userName);
    Optional<User> findByName(String name);
    Optional<User> findByAssignment(String assignment);
    Optional<User> findByRolesIn(Set<Roles> roles);
}
