package com.credit.bladi.config.security;

import com.credit.bladi.service.security.ICurrentActorProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentActorProvider implements ICurrentActorProvider {

    @Override
    public String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean authenticated = auth != null && auth.isAuthenticated()
            && !"anonymousUser".equals(auth.getPrincipal());
        return authenticated ? auth.getName() : null;
    }
}