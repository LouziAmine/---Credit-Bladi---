package com.credit.bladi.config.security;

import com.credit.bladi.config.web.ClientIpResolver;
import com.credit.bladi.service.auth.ITokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LogManager.getLogger(JwtAuthFilter.class);

    private final ITokenService tokenService;
    private final UserDetailsService userDetailsService;
    private final CookieTokenHelper cookieTokenHelper;

    public JwtAuthFilter(ITokenService tokenService,
                         UserDetailsService userDetailsService,
                         CookieTokenHelper cookieTokenHelper) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
        this.cookieTokenHelper = cookieTokenHelper;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String token = cookieTokenHelper.extractToken(request);
        if (token != null) {
            try {
                String username = tokenService.extractUsername(token);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    if (tokenService.isTokenValid(token, userDetails.getUsername())) {
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            } catch (Exception e) {
                LOGGER.warn("Rejected token from [{}]: {}", ClientIpResolver.resolve(request), e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }
}