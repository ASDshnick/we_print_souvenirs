package com.weprintsouvenirs.we_print_souvenirs.user.repository;

import com.weprintsouvenirs.we_print_souvenirs.user.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByUsername(String username);

    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsernameAndIdNot(String username, Long id);
}
