package com.credit.bladi.controller.auth;

import static com.credit.bladi.controller.auth.mapper.AuthControllerMapper.toAuthUserResponseDTO;
import static com.credit.bladi.controller.auth.mapper.AuthControllerMapper.toUser;

import com.credit.bladi.config.security.CookieTokenHelper;
import com.credit.bladi.controller.dto.auth.request.LoginRequestDTO;
import com.credit.bladi.controller.dto.auth.request.RegisterRequestDTO;
import com.credit.bladi.controller.dto.auth.response.AuthUserResponseDTO;
import com.credit.bladi.model.auth.AuthLoginResult;
import com.credit.bladi.service.auth.IAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController implements IAuthController {

    private final IAuthService authService;
    private final CookieTokenHelper cookieTokenHelper;

    public AuthController(IAuthService authService, CookieTokenHelper cookieTokenHelper) {
        this.authService = authService;
        this.cookieTokenHelper = cookieTokenHelper;
    }

    @Override
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequestDTO dto) {
        authService.register(toUser(dto));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<AuthUserResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto,
                                                     HttpServletResponse response) {
        AuthLoginResult result = authService.login(toUser(dto));
        setAuthCookies(response, result);
        return ResponseEntity.ok(toAuthUserResponseDTO(result));
    }

    @Override
    @PostMapping("/refresh")
    public ResponseEntity<AuthUserResponseDTO> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieTokenHelper.extractRefreshToken(request);
        AuthLoginResult result = authService.refresh(refreshToken);
        setAuthCookies(response, result);
        return ResponseEntity.ok(toAuthUserResponseDTO(result));
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = cookieTokenHelper.extractToken(request);
        String refreshToken = cookieTokenHelper.extractRefreshToken(request);
        authService.logout(accessToken, refreshToken);
        response.addHeader(HttpHeaders.SET_COOKIE, cookieTokenHelper.buildAuthCookie("", 0).toString());
        response.addHeader(HttpHeaders.SET_COOKIE, cookieTokenHelper.buildRefreshCookie("", 0).toString());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    @GetMapping("/me")
    public ResponseEntity<AuthUserResponseDTO> me() {
        return ResponseEntity.ok(toAuthUserResponseDTO(SecurityContextHolder.getContext().getAuthentication()));
    }

    private void setAuthCookies(HttpServletResponse response, AuthLoginResult result) {
        int maxAge = cookieTokenHelper.getCookieMaxAgeSeconds();
        int refreshMaxAge = cookieTokenHelper.getRefreshCookieMaxAgeSeconds();
        response.addHeader(HttpHeaders.SET_COOKIE,
            cookieTokenHelper.buildAuthCookie(result.accessToken(), maxAge).toString());
        response.addHeader(HttpHeaders.SET_COOKIE,
            cookieTokenHelper.buildRefreshCookie(result.refreshToken(), refreshMaxAge).toString());
    }
}