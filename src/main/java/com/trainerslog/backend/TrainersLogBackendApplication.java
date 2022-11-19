package com.trainerslog.backend;

import com.trainerslog.backend.lib.entities.Role;
import com.trainerslog.backend.lib.entities.User;
import com.trainerslog.backend.lib.types.UserRoles;
import com.trainerslog.backend.services.RoleService;
import com.trainerslog.backend.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TrainersLogBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrainersLogBackendApplication.class, args);
	}

	//TODO: remove this in the future
//	@Bean
//	CommandLineRunner run(UserService userService, RoleService roleService) {
//		return args -> {
//			try {
//				User admin = new User();
//				admin.setUsername("Daci");
//				admin.setPassword("Daci123");
//				userService.createUser(admin);
//
//				roleService.createRole(new Role(null, UserRoles.ADMIN));
//
//				userService.addRoleToUser("Daci", "ADMIN");
//			} catch (Exception ignored) {}
//
//		};
//	}

}
