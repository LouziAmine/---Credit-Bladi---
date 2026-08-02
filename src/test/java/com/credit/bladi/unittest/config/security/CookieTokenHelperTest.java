package com.credit.bladi.unittest.config.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.credit.bladi.config.security.CookieTokenHelper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;

@ExtendWith(MockitoExtension.class)
class CookieTokenHelperTest {

    @Mock
    private HttpServletRequest request;

    private CookieTokenHelper helper;

    @BeforeEach
    void setUp() throws Exception {
        helper = new CookieTokenHelper();
        setField(helper, "secureCookie", false);
        setField(helper, "accessCookieMaxAgeSeconds", 3600);
        setField(helper, "refreshCookieMaxAgeSeconds", 604800);
    }

    private void setField(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void shouldReturnCookieMaxAgeSeconds() {
        assertEquals(3600, helper.getCookieMaxAgeSeconds());
    }

    @Test
    void shouldReturnRefreshCookieMaxAgeSeconds() {
        assertEquals(604800, helper.getRefreshCookieMaxAgeSeconds());
    }

    @Test
    void shouldHaveAccessCookieNameConstant() {
        assertNotNull(CookieTokenHelper.ACCESS_COOKIE_NAME);
        assertEquals("cb_access_token", CookieTokenHelper.ACCESS_COOKIE_NAME);
    }

    @Test
    void shouldHaveRefreshCookieNameConstant() {
        assertNotNull(CookieTokenHelper.REFRESH_COOKIE_NAME);
        assertEquals("cb_refresh_token", CookieTokenHelper.REFRESH_COOKIE_NAME);
    }

    @Test
    void shouldBuildAuthCookieWithCorrectName() {
        ResponseCookie cookie = helper.buildAuthCookie("token123", 3600);
        assertNotNull(cookie);
        assertEquals(CookieTokenHelper.ACCESS_COOKIE_NAME, cookie.getName());
        assertEquals("token123", cookie.getValue());
        assertTrue(cookie.isHttpOnly());
    }

    @Test
    void shouldBuildRefreshCookieWithCorrectName() {
        ResponseCookie cookie = helper.buildRefreshCookie("refresh123", 604800);
        assertNotNull(cookie);
        assertEquals(CookieTokenHelper.REFRESH_COOKIE_NAME, cookie.getName());
        assertEquals("refresh123", cookie.getValue());
        assertTrue(cookie.isHttpOnly());
    }

    @Test
    void shouldExtractTokenFromCookie() {
        Cookie accessCookie = new Cookie(CookieTokenHelper.ACCESS_COOKIE_NAME, "mytoken");
        when(request.getCookies()).thenReturn(new Cookie[]{accessCookie});

        String token = helper.extractToken(request);

        assertEquals("mytoken", token);
    }

    @Test
    void shouldExtractTokenFromCookieWhenOtherUnrelatedCookiesPresent() {
        Cookie unrelated = new Cookie("other_cookie", "irrelevant");
        Cookie accessCookie = new Cookie(CookieTokenHelper.ACCESS_COOKIE_NAME, "mytoken");
        when(request.getCookies()).thenReturn(new Cookie[]{unrelated, accessCookie});

        String token = helper.extractToken(request);

        assertEquals("mytoken", token);
    }

    @Test
    void shouldFallBackToAuthHeaderWhenCookiesPresentButNoneMatch() {
        Cookie unrelated = new Cookie("other_cookie", "irrelevant");
        when(request.getCookies()).thenReturn(new Cookie[]{unrelated});
        when(request.getHeader("Authorization")).thenReturn("Bearer headertoken");

        String token = helper.extractToken(request);

        assertEquals("headertoken", token);
    }

    @Test
    void shouldExtractTokenFromAuthorizationHeaderWhenNoCookie() {
        when(request.getCookies()).thenReturn(null);
        when(request.getHeader("Authorization")).thenReturn("Bearer headertoken");

        String token = helper.extractToken(request);

        assertEquals("headertoken", token);
    }

    @Test
    void shouldReturnNullWhenNoTokenPresent() {
        when(request.getCookies()).thenReturn(null);
        when(request.getHeader("Authorization")).thenReturn(null);

        String token = helper.extractToken(request);

        assertNull(token);
    }

    @Test
    void shouldReturnNullWhenAuthHeaderIsNotBearer() {
        when(request.getCookies()).thenReturn(null);
        when(request.getHeader("Authorization")).thenReturn("Basic abc123");

        String token = helper.extractToken(request);

        assertNull(token);
    }

    @Test
    void shouldExtractRefreshTokenFromCookie() {
        Cookie refreshCookie = new Cookie(CookieTokenHelper.REFRESH_COOKIE_NAME, "refreshtoken");
        when(request.getCookies()).thenReturn(new Cookie[]{refreshCookie});

        String token = helper.extractRefreshToken(request);

        assertEquals("refreshtoken", token);
    }

    @Test
    void shouldReturnNullWhenCookiesPresentButNoneMatchRefreshToken() {
        Cookie unrelated = new Cookie("other_cookie", "irrelevant");
        when(request.getCookies()).thenReturn(new Cookie[]{unrelated});

        String token = helper.extractRefreshToken(request);

        assertNull(token);
    }

    @Test
    void shouldReturnNullRefreshTokenWhenNoCookies() {
        when(request.getCookies()).thenReturn(null);

        String token = helper.extractRefreshToken(request);

        assertNull(token);
    }
}