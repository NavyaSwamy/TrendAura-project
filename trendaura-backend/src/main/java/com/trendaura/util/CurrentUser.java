package com.trendaura.util;

import com.trendaura.security.SecurityUser;
import org.springframework.security.core.context.SecurityContextHolder;

public final class CurrentUser {

    private CurrentUser() {}

    public static Long id() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof SecurityUser) {
            return ((SecurityUser) principal).getId();
        }
        throw new RuntimeException("Not authenticated");
    }
}
