package com.library.Library.Management.System.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.library.Library.Management.System.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}