package com.jintu.jintuaiagent.advisor;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 权限校验
 * Expects user permissions in request userParams.
 */
public class PermissionCheckAdvisor implements CallAdvisor, StreamAdvisor {

    public static final String USER_PERMISSIONS_KEY = "userPermissions";
    public static final String USER_ROLE_KEY = "userRole";
    public static final String ADMIN_ROLE = "admin";

    private final String requiredPermission;

    public PermissionCheckAdvisor(String requiredPermission) {
        this.requiredPermission = requiredPermission;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain chain) {
        checkPermission(chatClientRequest);
        return chain.nextCall(chatClientRequest);
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain chain) {
        checkPermission(chatClientRequest);
        return chain.nextStream(chatClientRequest);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    private void checkPermission(ChatClientRequest chatClientRequest) {
        if (!hasPermission(chatClientRequest)) {
            throw new IllegalStateException("Permission denied: " + requiredPermission);
        }
    }

    private boolean hasPermission(ChatClientRequest chatClientRequest) {
        if (requiredPermission == null || requiredPermission.isBlank()) {
            return true;
        }

        Object role = chatClientRequest.context().get(USER_ROLE_KEY);
        if (ADMIN_ROLE.equalsIgnoreCase(Objects.toString(role, ""))) {
            return true;
        }

        Object rawPermissions = chatClientRequest.context().get(USER_PERMISSIONS_KEY);
        Set<String> permissions = normalizePermissions(rawPermissions);
        return permissions.contains(requiredPermission);
    }

    private Set<String> normalizePermissions(Object rawPermissions) {
        Set<String> permissions = new HashSet<>();
        if (rawPermissions instanceof Collection<?> collection) {
            for (Object item : collection) {
                if (item != null) {
                    permissions.add(item.toString());
                }
            }
        } else if (rawPermissions instanceof String text) {
            Arrays.stream(text.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .forEach(permissions::add);
        } else if (rawPermissions != null) {
            permissions.add(rawPermissions.toString());
        }
        return permissions;
    }
}
