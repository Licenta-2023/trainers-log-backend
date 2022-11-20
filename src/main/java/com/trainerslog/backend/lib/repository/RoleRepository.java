package com.trainerslog.backend.lib.repository;

import com.trainerslog.backend.lib.entity.Role;
import com.trainerslog.backend.lib.types.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(UserRoles roleName);
}
