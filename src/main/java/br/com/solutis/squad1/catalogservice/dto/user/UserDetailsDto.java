package br.com.solutis.squad1.catalogservice.dto.user;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;

public record UserDetailsDto(
        String username,
        Set<SimpleGrantedAuthority> authorities
) {
}
