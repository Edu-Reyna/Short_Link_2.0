package com.example.demo;

import com.example.demo.entities.PermissionEntity;
import com.example.demo.entities.PermissionEnum;
import com.example.demo.entities.RoleEntity;
import com.example.demo.repositories.IRoleRepository;
import com.example.demo.repositories.IUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;
import java.util.Set;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class AcortadorApplication {

	public static void main(String[] args) {
		SpringApplication.run(AcortadorApplication.class, args);
	}

	@Bean
	CommandLineRunner init(IRoleRepository roleRepository, IUserRepository userRepository) {
		return args -> {

			PermissionEntity createLink = PermissionEntity.builder()
					.permissionEnum(PermissionEnum.CREATE_LINK)
					.build();
			PermissionEntity readLink = PermissionEntity.builder()
					.permissionEnum(PermissionEnum.READ_LINK)
					.build();
			PermissionEntity updateLink = PermissionEntity.builder()
					.permissionEnum(PermissionEnum.UPDATE_LINK)
					.build();
			PermissionEntity deleteLink = PermissionEntity.builder()
					.permissionEnum(PermissionEnum.DELETE_LINK)
					.build();
			PermissionEntity createUser = PermissionEntity.builder()
					.permissionEnum(PermissionEnum.CREATE_USER)
					.build();
			PermissionEntity readUser = PermissionEntity.builder()
					.permissionEnum(PermissionEnum.READ_USER)
					.build();
			PermissionEntity updateUser = PermissionEntity.builder()
					.permissionEnum(PermissionEnum.UPDATE_USER)
					.build();

			RoleEntity adminRole = RoleEntity.builder()
					.roleEnum(com.example.demo.entities.RoleEnum.ADMIN)
					.permissionList(Set.of(createLink, readLink, updateLink, deleteLink,
							createUser, readUser, updateUser))
					.build();
			RoleEntity userRole = RoleEntity.builder()
					.roleEnum(com.example.demo.entities.RoleEnum.USER)
					.permissionList(Set.of(createLink, readLink, updateLink, deleteLink))
					.build();

			roleRepository.saveAll(List.of(adminRole, userRole));
		};
	}
}
