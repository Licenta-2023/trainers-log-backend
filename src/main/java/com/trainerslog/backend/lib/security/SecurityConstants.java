package com.trainerslog.backend.lib.security;

import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class SecurityConstants {
    private static final String secret = System.getenv("SECRET_KEY");

    private static final Algorithm algorithm = Algorithm.HMAC256(SecurityConstants.getSecret());

    private static final Integer accessTokenExpirationMinutes = 60;

    private static final Integer refreshTokenExpirationMinutes = 60;

    private static final String[] permittedToAll = {
            "/api/user/login",
            "/api/user/register"
    };

    private static final String[] permittedToTrainer = {
            "/api/trainer/**"
    };

    private static final String[] permittedToUser = {
            "/api/user/refreshToken",
            "/api/reservation",
            "/api/reservation/users/*/months/*",
            "/api/reservation/users/*/months/*/days/*",
            "/api/trainer/usernames"
    };

    public static byte[] getSecret() {
        return secret.getBytes();
    }

    public static Algorithm getAlgorithm() {
        return algorithm;
    }

    public static Date getAccessTokenExpirationDate() {
        return new Date(System.currentTimeMillis() + accessTokenExpirationMinutes * 60 * 1000);
    }

    public static Date getRefreshTokenExpirationDate() {
        return new Date(System.currentTimeMillis() + refreshTokenExpirationMinutes * 60 * 1000);
    }

    public static String[] getAllowedRequestToAll() {
        return permittedToAll;
    }

    public static String[] getAllowedRequestToTrainer() {
        return permittedToTrainer;
    }

    public static String[] getAllowedRequestToUser() {
        return permittedToUser;
    }
}
