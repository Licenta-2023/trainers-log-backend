package com.trainerslog.backend.lib.util;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.trainerslog.backend.lib.exception.ClientException;

import java.util.List;

public class UserUtils {
    public static String getUsernameFromBearerToken(String bearerToken) {
        String token = bearerToken.substring("Bearer ".length());
        DecodedJWT decodedJWT = SecurityUtils.decodeJWT(token);
        return decodedJWT.getSubject();
    }

    public static List<String> getRoleFromBearerToken(String bearerToken) {
        String token = bearerToken.substring("Bearer ".length());
        DecodedJWT decodedJWT = SecurityUtils.decodeJWT(token);
        Claim roles = decodedJWT.getClaim("roles");
        return roles.asList(String.class);
    }

    public static void throwIfRequestUserTheSameAsTargetUser(String username, String bearerToken) {
        String requestUsername = UserUtils.getUsernameFromBearerToken(bearerToken);
        if (!username.equals(requestUsername)) {
            throw new ClientException(String.format("User %s cannot do any changes for an other user: %s", requestUsername, username));
        }
    }
}
