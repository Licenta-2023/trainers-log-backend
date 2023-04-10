package com.trainerslog.backend.lib.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trainerslog.backend.lib.entity.User;
import com.trainerslog.backend.lib.security.SecurityConstants;
import lombok.SneakyThrows;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class SecurityUtils {

    public static UsernamePasswordAuthenticationToken getAuthorizationAuthToken(String token) {
        DecodedJWT decodedJWT = decodeJWT(token);
        String username = decodedJWT.getSubject();
        String[] roles = Optional.ofNullable(decodedJWT.getClaim("roles").asArray(String.class)).orElse(new String[0]);

        return new UsernamePasswordAuthenticationToken(username, null, getAuthoritiesFromRoleStringArray(roles));
    }

    @SneakyThrows
    public static void writeToResponseBody(HttpServletResponse response, Map<String, String> body){
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }

    public static DecodedJWT decodeJWT(String token) {
        JWTVerifier verifier = JWT.require(SecurityConstants.getAlgorithm()).build();
        return verifier.verify(token);
    }

    public static String buildAccessTokenFromUserEntity(User user, HttpServletRequest request) {
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(SecurityConstants.getAccessTokenExpirationDate())
                .withIssuer(request.getRequestURI())
                .withClaim("roles", getAuthoritiesForUser(user))
                .sign(SecurityConstants.getAlgorithm());
    }

    public static String buildAccessTokenFromUser(org.springframework.security.core.userdetails.User user, HttpServletRequest request) {
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(SecurityConstants.getAccessTokenExpirationDate())
                .withIssuer(request.getRequestURI())
                .withClaim("roles", getAuthoritiesForUser(user))
                .sign(SecurityConstants.getAlgorithm());
    }

    public static String buildRefreshTokenFromUser(org.springframework.security.core.userdetails.User user, HttpServletRequest request) {
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(SecurityConstants.getRefreshTokenExpirationDate())
                .withIssuer(request.getRequestURI())
                .withClaim("roles", getAuthoritiesForUser(user))
                .withClaim("isRefreshToken", true)
                .sign(SecurityConstants.getAlgorithm());
    }

    private static Collection<SimpleGrantedAuthority> getAuthoritiesFromRoleStringArray(String[] roles) {
        return stream(roles)
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    private static List<String> getAuthoritiesForUser(org.springframework.security.core.userdetails.User user) {
        return user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    private static List<String> getAuthoritiesForUser(User user) {
        return user.getRoles().stream()
                .map(role -> role.getName().toString())
                .collect(Collectors.toList());
    }
}
