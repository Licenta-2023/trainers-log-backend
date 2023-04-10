package com.trainerslog.backend.lib.security.filter.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trainerslog.backend.lib.exception.ClientException;
import com.trainerslog.backend.lib.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    @Override
    @SneakyThrows
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (CorsUtils.isPreFlightRequest(request)) {
            response.setStatus(HttpServletResponse.SC_OK);
            return null;
        }
        Map<String, String> requestMap = new ObjectMapper().readValue(request.getInputStream(), Map.class);
        String username = Optional.of(requestMap.get("username")).orElseThrow(() -> new ClientException("No username provided"));
        String password = Optional.of(requestMap.get("password")).orElseThrow(() -> new ClientException("No password provided"));
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        String accessToken = SecurityUtils.buildAccessTokenFromUser(user, request);

        String refreshToken = SecurityUtils.buildRefreshTokenFromUser(user, request);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", refreshToken);
        SecurityUtils.writeToResponseBody(response, tokens);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        String errorClass = failed.getCause() == null ? failed.getClass().getSimpleName() : failed.getCause().getClass().getSimpleName();
        Map<String, String> errorMap = Map.of(
                "error_message", failed.getMessage(),
                "error_class", errorClass
        );
        SecurityUtils.writeToResponseBody(response, errorMap);
    }

}
