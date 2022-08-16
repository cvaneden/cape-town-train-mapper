package com.ptjp.application.data.generator;

import com.ptjp.application.data.Role;
import com.ptjp.application.data.entity.User;
import com.ptjp.application.data.entity.Users;
import com.ptjp.application.data.service.UserRepository;
import com.ptjp.application.data.service.UsersRepository;
import com.vaadin.exampledata.DataType;
import com.vaadin.exampledata.ExampleDataGenerator;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringComponent
public class DataGenerator {

    @Bean
    public CommandLineRunner loadData(PasswordEncoder passwordEncoder, UserRepository userRepository,
            UsersRepository usersRepository) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (userRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }
            int seed = 123;

            logger.info("Generating demo data");

            logger.info("... generating 2 User entities...");
            User user = new User();
            user.setName("Conor Van Eden");
            user.setUsername("user");
            user.setHashedPassword(passwordEncoder.encode("user"));
            //user.setProfilePictureUrl("https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
            user.setRoles(Collections.singleton(Role.USER));
            userRepository.save(user);
            User admin = new User();
            admin.setName("Conor Van Eden");
            admin.setUsername("admin");
            admin.setHashedPassword(passwordEncoder.encode("admin"));
            // admin.setProfilePictureUrl("https://images.unsplash.com/photo-1607746882042-944635dfe10e?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
            admin.setRoles(Set.of(Role.USER, Role.ADMIN));
            userRepository.save(admin);
            logger.info("... generating 100 Users entities...");
            ExampleDataGenerator<Users> usersRepositoryGenerator = new ExampleDataGenerator<>(Users.class,
                    LocalDateTime.of(2022, 8, 16, 0, 0, 0));
            usersRepositoryGenerator.setData(Users::setFirstName, DataType.FIRST_NAME);
            usersRepositoryGenerator.setData(Users::setLastName, DataType.LAST_NAME);
            usersRepositoryGenerator.setData(Users::setEmail, DataType.EMAIL);
            usersRepositoryGenerator.setData(Users::setPhone, DataType.PHONE_NUMBER);
            usersRepository.saveAll(usersRepositoryGenerator.create(100, seed));

            logger.info("Generated demo data");
        };
    }

}