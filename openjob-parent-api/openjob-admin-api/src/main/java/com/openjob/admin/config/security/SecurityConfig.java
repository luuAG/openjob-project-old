package com.openjob.admin.config.security;

import com.openjob.admin.adminuser.AdminUserService;
import com.openjob.admin.config.ConfigProperty;
import com.openjob.admin.config.filter.CustomAuthenticationFilter;
import com.openjob.admin.config.filter.CustomAuthorizationFilter;
import com.openjob.common.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ConfigProperty configProperties;
    private final AdminUserService adminUserService;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests().antMatchers("/login", "/token/refresh").permitAll();
        http.authorizeRequests()
                .antMatchers("/adminuser/create/**", "/adminuser/activate/**", "/adminuser/deactivate/**")
                .hasAuthority(Role.SUPER_ADMIN.name());
        http.authorizeRequests()
                .antMatchers("/**")
                .hasAnyAuthority(Role.SUPER_ADMIN.name(), Role.ADMIN.name());
        http.authorizeRequests().anyRequest().authenticated();
        http.addFilter(new CustomAuthenticationFilter(authenticationManagerBean(), configProperties, adminUserService));
        http.addFilterBefore(new CustomAuthorizationFilter(configProperties), CustomAuthenticationFilter.class);
//        http.authorizeRequests().anyRequest().permitAll();
    }


}
