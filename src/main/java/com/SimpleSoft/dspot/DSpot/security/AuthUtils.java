package com.SimpleSoft.dspot.DSpot.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtils {

    public static String getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .findFirst()
                .map(granted -> granted.getAuthority().replace("ROLE_", ""))
                .orElse("UNKNOWN");
    }

    public static String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.getPrincipal() instanceof String)
                ? (String) auth.getPrincipal()
                : null;
    }

    public static Long getCurrentDistributorId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .filter(a -> a.getAuthority().startsWith("DISTRIBUTOR_"))
                .findFirst()
                .map(a -> Long.parseLong(a.getAuthority().replace("DISTRIBUTOR_", "")))
                .orElseThrow(() -> new RuntimeException("Distributor ID not found in token"));
    }
}
