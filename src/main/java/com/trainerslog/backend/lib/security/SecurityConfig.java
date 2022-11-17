package com.trainerslog.backend.lib.security;

import com.trainerslog.backend.lib.security.filter.CustomAuthenticationFilter;
import com.trainerslog.backend.lib.security.filter.CustomAuthorizationFilter;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private  final UserDetailsService userDetailsService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final String[] permittedToAll = {
            "/api/user/login",
            "/api/user/register",
            "/api/user/refreshToken"
    };

    private final String[] permittedToTrainer = {

    };

    private final String[] permittedToUser = {

    };

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean());
        customAuthenticationFilter.setFilterProcessesUrl("/api/user/login");

        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Endpoints permitted to all request
        http.authorizeHttpRequests()
                .antMatchers(permittedToAll).permitAll();

        //Endpoints permitted only to ADMIN
        http.authorizeHttpRequests()
                .antMatchers("/api/**").hasAuthority("ADMIN");

        //Endpoints permitted only to TRAINER
        http.authorizeHttpRequests()
                .antMatchers(permittedToTrainer).hasAuthority("TRAINER");

        //Endpoints permitted only to USER
        http.authorizeHttpRequests()
                .antMatchers(permittedToUser).hasAnyAuthority("USER", "TRAINER");

        http.authorizeHttpRequests().anyRequest().authenticated();

        http.addFilter(customAuthenticationFilter);
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
