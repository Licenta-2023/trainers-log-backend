package com.trainerslog.backend.repositories;

import com.trainerslog.backend.entities.Role;
import com.trainerslog.backend.lib.types.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(UserRoles roleName);
}
