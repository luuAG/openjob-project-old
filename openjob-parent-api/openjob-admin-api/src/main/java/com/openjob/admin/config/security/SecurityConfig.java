package com.openjob.admin.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openjob.admin.adminuser.AdminUserService;
import com.openjob.admin.config.ConfigProperty;
import com.openjob.admin.config.filter.CustomAuthenticationFilter;
import com.openjob.admin.config.filter.CustomAuthorizationFilter;
import com.openjob.common.enums.Role;
import com.openjob.common.response.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.ServletOutputStream;

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
        http.cors();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests().antMatchers("/login",
                "/token/refresh",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/webjars/**",
                "/v2/**",
                "/swagger-resources/**").permitAll();
        http.authorizeRequests()
                .antMatchers("/adminuser/create/**", "/adminuser/update/**","/adminuser/activate/**", "/adminuser/deactivate/**")
                .hasAuthority(Role.SUPER_ADMIN.name());
        http.authorizeRequests()
                .antMatchers("/**")
                .hasAnyAuthority(Role.SUPER_ADMIN.name(), Role.ADMIN.name());
        http.authorizeRequests().anyRequest().authenticated();
        http.addFilter(new CustomAuthenticationFilter(authenticationManagerBean(), configProperties, adminUserService));
        http.addFilterBefore(new CustomAuthorizationFilter(configProperties), CustomAuthenticationFilter.class);
        http.exceptionHandling().accessDeniedHandler(accessDeniedHandler());

    }
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, ex) -> {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            ServletOutputStream out = response.getOutputStream();
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setErrorMessage("User does not have permission. "+ex.getMessage());
            errorResponse.setErrorCode(HttpStatus.FORBIDDEN.value());
            new ObjectMapper().writeValue(out, errorResponse);
            out.flush();
        };
    }

    @Bean
    public CorsFilter corsFilter() {

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        //config.setAllowCredentials(true); // you USUALLY want this
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("HEAD");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("PATCH");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
