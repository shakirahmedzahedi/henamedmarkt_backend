package com.saz.se.goat.config;

import com.saz.se.goat.auth.AuthenticationService;
import com.saz.se.goat.utils.GoatUserDetailsService;
import com.saz.se.goat.utils.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;

    @Autowired
    ApplicationContext context;
    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        logger.info("-->"+request.getMethod()+" "+ request.getRequestURI().toString()+" "+request.getQueryString()+ " <--");
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        logger.info("AuthHeader: "+authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            logger.info("Already have token");
            token = authHeader.substring(7).trim();
            username = jwtService.extractUserName(token);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = context.getBean(GoatUserDetailsService.class).loadUserByUsername(username);
            if (jwtService.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource()
                        .buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}*/

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ApplicationContext context;

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        logger.info("-->" + request.getMethod() + " " + request.getRequestURI() + " " + request.getQueryString() + " <--");
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        logger.info("AuthHeader: " + authHeader);

        try {
            // Check if the Authorization header contains a Bearer token
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                logger.info("Token present in header");
                token = authHeader.substring(7).trim();
                username = jwtService.extractUserName(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = context.getBean(GoatUserDetailsService.class).loadUserByUsername(username);

                    // Validate the token
                    if (jwtService.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    } else {
                        logger.error("Token is invalid or expired");
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("Token expired or invalid. Please log in again.");
                        return; // Stop further processing
                    }
                }
            }
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            logger.error("Token has expired", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token has expired. Please log in again.");
            return; // Stop further processing
        } catch (Exception e) {
            logger.error("Error processing the token", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Authentication error. Please log in again.");
            return; // Stop further processing
        }

        // Continue with the filter chain if no issues
        filterChain.doFilter(request, response);
    }
}

