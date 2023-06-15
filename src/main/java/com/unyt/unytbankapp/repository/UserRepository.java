package com.unyt.unytbankapp.repository;

import com.unyt.unytbankapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByEmail (String email);
}
