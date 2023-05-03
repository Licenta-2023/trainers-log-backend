package com.trainerslog.backend;

import com.trainerslog.backend.lib.entity.Role;
import com.trainerslog.backend.lib.entity.User;
import com.trainerslog.backend.lib.repository.UserRepository;
import com.trainerslog.backend.lib.types.UserRoles;
import com.trainerslog.backend.service.RoleService;
import com.trainerslog.backend.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
public class TrainersLogBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrainersLogBackendApplication.class, args);
	}

	@Bean
	CommandLineRunner run(UserService userService, RoleService roleService, UserRepository userRepository) {
		return args -> {
			try {
				User admin = new User();
				admin.setUsername("ADMIN");
				admin.setPassword("Daci123");
				admin.setDob(LocalDate.now());
				admin.setFirstName("ADMIN");
				admin.setLastName("ADMIN");


				roleService.createRole(new Role(null, UserRoles.ADMIN));
				roleService.createRole(new Role(null, UserRoles.TRAINER));
				roleService.createRole(new Role(null, UserRoles.USER));

				userRepository.save(userService.createUser(admin));

				userService.addRoleToUser("ADMIN", "ADMIN");
			} catch (Exception ignored) {}
		};
	}
}
