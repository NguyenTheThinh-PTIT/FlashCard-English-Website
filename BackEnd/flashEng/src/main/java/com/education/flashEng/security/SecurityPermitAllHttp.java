package com.education.flashEng.security;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class SecurityPermitAllHttp {
    private static final Map<String, Set<String>> PERMIT_ALL_ENDPOINTS = new HashMap<>();

    static {
        PERMIT_ALL_ENDPOINTS.put("/user/login", Set.of("POST"));
        PERMIT_ALL_ENDPOINTS.put("/user/register", Set.of("POST"));
        PERMIT_ALL_ENDPOINTS.put("/class/public/{classId}", Set.of("GET"));
        PERMIT_ALL_ENDPOINTS.put("/class", Set.of("GET"));
    }

    public static Map<String, Set<String>> getPermitAllEndpoints() {
        return PERMIT_ALL_ENDPOINTS;
    }
}
