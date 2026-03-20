package com.saltedgeproxy.app.saltedgeproxy.repository;

import com.saltedgeproxy.app.saltedgeproxy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

}
