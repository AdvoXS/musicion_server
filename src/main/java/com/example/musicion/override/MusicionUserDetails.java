package com.example.musicion.override;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class MusicionUserDetails extends User {
    com.example.musicion.model.auth.User user;

    public MusicionUserDetails(com.example.musicion.model.auth.User user, Collection<? extends GrantedAuthority> authorities) {
        super(user.getUsername(), user.getPassword(), authorities);
        this.user = user;
    }

    public MusicionUserDetails(com.example.musicion.model.auth.User user, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(user.getUsername(), user.getPassword(), user.isEnabled(), accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.user = user;
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
}
