package com.gfreitash.flight_booking.config.security;

import com.gfreitash.flight_booking.entities.Role;
import com.gfreitash.flight_booking.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final RoleRepository roleRepository;

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_EMPLOYEE = "EMPLOYEE";
    private static final String ROLE_USER = "USER";
    private static final String ROLE_GUEST = "GUEST";

    private RoleHierarchy roleHierarchy;

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchyImpl = new RoleHierarchyImpl();

        List<Role> roles = roleRepository.findAll();

        StringBuilder hierarchy = new StringBuilder();
        for (Role role : roles) {
            if (role.getParentRole() != null) {
                if(hierarchy.length() > 0) hierarchy.append("\n");
                hierarchy.append(role.getParentRole().getName()).append(" > ");
                hierarchy.append(role.getName());
            }
        }


        roleHierarchyImpl.setHierarchy(hierarchy.toString());
        this.roleHierarchy = roleHierarchyImpl;
        return roleHierarchyImpl;
    }


    @Bean
    public DefaultWebSecurityExpressionHandler expressionHandler(RoleHierarchy hierarchy) {
        DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(hierarchy);
        return expressionHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/api/auth/**").permitAll()

                .requestMatchers(multipleHttpMethodsMatcher("/api/roles/**",
                        HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)).access(hasRole(ROLE_ADMIN))
                .requestMatchers(HttpMethod.GET, "/api/roles/**").access(hasRole(ROLE_EMPLOYEE))

                .requestMatchers(multipleHttpMethodsMatcher("/api/users/**",
                        HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)).access(hasRole(ROLE_ADMIN))
                .requestMatchers(HttpMethod.GET, "/api/users/**").access(hasRole(ROLE_EMPLOYEE))

                .requestMatchers(multipleHttpMethodsMatcher("/api/airports/**",
                        HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)).access(hasRole(ROLE_ADMIN))
                .requestMatchers(HttpMethod.GET, "/api/airports/**").access(hasRole(ROLE_GUEST))

                .anyRequest().authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    public RequestMatcher multipleHttpMethodsMatcher(String pattern, HttpMethod... httpMethods) {
        return new MultipleHttpMethodsRequestMatcher(pattern, httpMethods);
    }

    /**
     * This method is a temporary workaround for the issue described
     * <a href=https://github.com/spring-projects/spring-security/issues/12766#issuecomment-1444462772>here</a>
     * relating to the configuration of RoleHierarchy beans not being fully ported to spring security 6.0.x.
     * This should be fixed by Spring Security 6.1.*
     * @param role The role to check for
     * @return An {@link AuthorityAuthorizationManager} that checks if the user has the given role/sub-role
     */
    public AuthorityAuthorizationManager<RequestAuthorizationContext> hasRole(String role) {
        AuthorityAuthorizationManager<RequestAuthorizationContext> hasRole = AuthorityAuthorizationManager.hasRole(role);
        hasRole.setRoleHierarchy(this.roleHierarchy);
        return hasRole;
    }
}
