package com.trainerslog.backend.lib.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "\"user\"",
        uniqueConstraints = @UniqueConstraint(columnNames = {"username"})
)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    private String firstName;

    private String lastName;

    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDate dob;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"),
            uniqueConstraints = { @UniqueConstraint(columnNames = {"role_id", "user_id"}) }
    )
    @JsonIgnore
    private Collection<Role> roles = new ArrayList<>();
}
