package com.gfreitash.flight_booking.config.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * This class is used to match multiple HTTP methods to a single path.
 * It is useful when you want to define the same rules for a path with different HTTP methods.
 * For example, you can define the same rules for POST, PUT and DELETE methods in a single line
 * while keeping a separate rule for GET method:
 *<pre>
 *     http.authorizeRequests()
 *     .requestMatchers(new MultipleHttpMethodsRequestMatcher("/api/roles/**",
 *        HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)).hasRole("ADMIN")
 *        .requestMatchers(HttpMethod.GET, "/api/roles/**").hasAnyRole("ADMIN", "USER");
 *</pre>
 */
@Slf4j
public class MultipleHttpMethodsRequestMatcher implements RequestMatcher {

    private final AntPathRequestMatcher[] matcher;
    private int matchIndex = -1;

    public MultipleHttpMethodsRequestMatcher(String pattern, HttpMethod... httpMethods) {
        if(!pattern.startsWith("/")) {
            pattern = "/" + pattern;
        }
        
        matcher = new AntPathRequestMatcher[httpMethods.length];
        for (int i = 0; i < httpMethods.length; i++) {
            matcher[i] = new AntPathRequestMatcher(pattern, httpMethods[i].name());
        }
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        var matches = false;
        for (int i = 0; i < matcher.length; i++) {
            matches = matcher[i].matches(request);
            log.info(matcher[i].toString());
            log.info("request: " + request.getRequestURI());
            log.info("method matches: " + HttpMethod.valueOf(request.getMethod()));
            log.info("matches: " + matches);
            if (matches) {
                matchIndex = i;
                break;
            }
        }
        return matches;
    }

    @Override
    public MatchResult matcher(HttpServletRequest request) {
        return !matches(request) ? MatchResult.notMatch() : matcher[matchIndex].matcher(request);
    }
}
