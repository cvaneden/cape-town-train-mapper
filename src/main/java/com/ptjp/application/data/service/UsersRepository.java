package com.ptjp.application.data.service;

import com.ptjp.application.data.entity.Users;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, UUID> {

}