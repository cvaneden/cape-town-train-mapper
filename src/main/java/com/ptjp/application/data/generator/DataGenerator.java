package com.ptjp.application.data.generator;

import com.ptjp.application.data.Role;
import com.ptjp.application.data.entity.User;
import com.ptjp.application.data.service.UserRepository;
import com.ptjp.application.data.service.UserService;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;


@SpringComponent
public class DataGenerator {

    @Bean
    public CommandLineRunner loadData(PasswordEncoder passwordEncoder, UserService userService) {
        return args -> {

            // 3 admin roles are set, cannot be changed unless access to code base.
            userService.update(new User("conorveAdmin_","Conor Van Eden", passwordEncoder.encode("123"), Role.ADMIN));
            userService.update(new User("zacschmidtAdmin_","Zac Schmidt", passwordEncoder.encode("123"), Role.ADMIN));
            userService.update(new User("michaelwadeAdmin", "Michael Wade", passwordEncoder.encode("123"), Role.ADMIN));

            // 1 user role for testing
            userService.update(new User("bailey", "Bailey Van Eden", passwordEncoder.encode("123"), Role.USER, "Cape Town - Paris; Mowbray - Wynberg; Rondebosch - Stellenbosch; Athlone - Belville;"));
        };
    }
}
