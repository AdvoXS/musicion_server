package com.example.musicion.init;

import com.example.musicion.model.auth.Privilege;
import com.example.musicion.model.auth.Role;
import com.example.musicion.model.auth.User;
import com.example.musicion.repository.PrivilegeRepository;
import com.example.musicion.repository.RoleRepository;
import com.example.musicion.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SetupUsersLoader implements
        ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (alreadySetup)
            return;
        Privilege readPrivilege
                = createPrivilegeIfNotFound("READ_PRIVILEGE");
        Privilege writePrivilege
                = createPrivilegeIfNotFound("WRITE_PRIVILEGE");

        List<Privilege> adminPrivileges = Arrays.asList(
                readPrivilege, writePrivilege);
        createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        createRoleIfNotFound("ROLE_USER", Collections.singletonList(readPrivilege));

        Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow(() -> new RuntimeException("No role found"));
        User user = userRepository.findByUsername("Admin".toLowerCase()).orElseGet(() -> {
            User user1 = new User();
            user1.setUsername("Admin".toLowerCase());
            user1.setFirstName("Admin");
            user1.setLastName("Admin");
            user1.setPassword(passwordEncoder.encode("test"));
            user1.setEmail("admin@admin.com");
            user1.setRoles(Collections.singletonList(adminRole));
            userRepository.save(user1);
            return user1;
        });

        alreadySetup = true;
    }

    @Transactional
    Privilege createPrivilegeIfNotFound(String name) {
        return privilegeRepository.findByName(name).orElse(privilegeRepository.save(new Privilege(name)));
    }

    @Transactional
    void createRoleIfNotFound(
            String name, Collection<Privilege> privileges) {
        roleRepository.findByName(name).orElseGet(() -> {
            Role role = new Role(name);
            role.setPrivileges(privileges);
            return roleRepository.save(role);
        });
    }
}