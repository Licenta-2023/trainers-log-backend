package com.trainerslog.backend.lib.security;

import com.auth0.jwt.algorithms.Algorithm;
public class SecurityConstants {
    private static final byte[] secret = "secret".getBytes();

    private static final Algorithm algorithm = Algorithm.HMAC256(SecurityConstants.getSecret());

    private static final Integer accessTokenExpirationMinutes = 10;

    private static final Integer refreshTokenExpirationMinutes = 60;

    public static byte[] getSecret() {
        return secret;
    }

    public static Algorithm getAlgorithm() {
        return algorithm;
    }

    public static Integer getAccessTokenExpirationMinutes() {
        return accessTokenExpirationMinutes * 60 * 1000;
    }

    public static Integer getRefreshTokenExpirationMinutes() {
        return refreshTokenExpirationMinutes * 60 * 1000;
    }
}
