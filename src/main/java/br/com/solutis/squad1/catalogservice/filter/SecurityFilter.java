package br.com.solutis.squad1.catalogservice.filter;

import br.com.solutis.squad1.catalogservice.dto.user.UserDetailsDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getToken(request);

        if (token != null) {
            UserDetailsDto userDetailsDto = getUserDetails(request);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetailsDto.username(),
                    null,
                    userDetailsDto.authorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private UserDetailsDto getUserDetails(HttpServletRequest request) {
        String username = request.getHeader("User-name");
        Set<SimpleGrantedAuthority> authorities = getAuthorities(request);

        return new UserDetailsDto(username, authorities);
    }

    private Set<SimpleGrantedAuthority> getAuthorities(HttpServletRequest request) {
        String authoritiesHeader = request.getHeader("User-authorities");

        if (authoritiesHeader != null) {
            String[] authoritiesArray = Arrays.stream(authoritiesHeader.split(","))
                    .map(authority -> authority.replace("[", "").replace("]", "").trim())
                    .toArray(String[]::new);

            return Arrays.stream(authoritiesArray)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
        }

        return Collections.emptySet();
    }


    private String getToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token != null) {
            return token.replace("Bearer ", "").trim();
        }

        return null;
    }
}
