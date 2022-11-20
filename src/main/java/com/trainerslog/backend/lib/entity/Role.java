package com.trainerslog.backend.lib.entity;

import com.trainerslog.backend.lib.types.UserRoles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(
        uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})}
)
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated
    @Column(unique = true)
    @NotNull(message = "Role name cannot be empty")
    private UserRoles name;
}
