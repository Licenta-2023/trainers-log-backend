package com.trainerslog.backend.lib.security.filter.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trainerslog.backend.lib.exception.ClientException;
import com.trainerslog.backend.lib.security.filter.security.CachedBodyHttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class ValidateLoginDataFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if( request.getRequestURI().equals("/api/user/login")) {
            try {
                CachedBodyHttpServletRequest cachedBodyHttpServletRequest = new CachedBodyHttpServletRequest(request);
                Map<String, String> requestMap = new ObjectMapper().readValue(cachedBodyHttpServletRequest.getInputStream(), Map.class);
                String username = Optional.ofNullable(requestMap.get("username")).orElseThrow(() -> new ClientException("No username provided"));
                String password = Optional.ofNullable(requestMap.get("password")).orElseThrow(() -> new ClientException("No password provided"));
                if (username.equals("") || password.equals("")) {
                    throw new ClientException("Empty field.");
                }
                filterChain.doFilter(cachedBodyHttpServletRequest, response);
            } catch(ClientException e) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.getWriter().write(e.getMessage());
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
