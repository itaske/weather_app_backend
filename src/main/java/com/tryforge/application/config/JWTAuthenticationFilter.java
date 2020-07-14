package com.tryforge.application.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

public class JWTAuthenticationFilter  extends UsernamePasswordAuthenticationFilter {

    final ObjectMapper objectMapper = new ObjectMapper();


    public JWTAuthenticationFilter (){

    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try{
            com.tryforge.application.model.User creds = new ObjectMapper()
                    .readValue(request.getInputStream(), com.tryforge.application.model.User.class);

            UsernamePasswordAuthenticationToken  token = new UsernamePasswordAuthenticationToken(
                    creds.getEmail(),
                    creds.getPassword(),
                    new ArrayList<>()
            );

            //setDetails(request, token);
            Authentication auth = this.getAuthenticationManager().authenticate(token);
            System.out.println("Principal "+auth.getPrincipal().toString());
            return auth;
        } catch(IOException io){
            io.printStackTrace();
            throw new RuntimeException(io);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        String token = com.auth0.jwt.JWT.create()
                .withSubject(((User)authResult.getPrincipal()).getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .sign(HMAC512(SecurityConstants.SECRET.getBytes()));

        response.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json");
        Map<String,Object> map = new HashMap<>();
        map.put("token", token);
        map.put("email", ((User) authResult.getPrincipal()).getUsername());
        objectMapper.writeValue(response.getWriter(), map);
    }
}
