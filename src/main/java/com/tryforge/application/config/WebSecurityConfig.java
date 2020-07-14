package com.tryforge.application.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tryforge.application.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsServiceImpl userService;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;

    public WebSecurityConfig(UserDetailsServiceImpl userService, ObjectMapper objectMapper, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.objectMapper = objectMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public JWTAuthenticationFilter authenticationFilter() throws Exception {
        JWTAuthenticationFilter authenticationFilter = new JWTAuthenticationFilter();
        authenticationFilter.setAuthenticationFailureHandler(this::loginFailureHandler);
        authenticationFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/login", "POST"));
        authenticationFilter.setAuthenticationManager(authenticationManager());
        return authenticationFilter;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, SecurityConstants.SIGN_UP_URL)
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilter(new JWTAuthorizationFilter(authenticationManager()))
                .logout()
                .logoutUrl("/logout") // the URL on which the clients should post if they want to logout
                .logoutSuccessHandler(this::logoutSuccessHandler)
                .invalidateHttpSession(true)
                .and()
                .exceptionHandling()//default response if the client wants to get a resources unauthorized
                .authenticationEntryPoint(new Http403ForbiddenEntryPoint())
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        //this disables session creation on Spring Security
        //.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }


    private void loginFailureHandler(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        objectMapper.writeValue(response.getWriter(),"Login Failed !");
    }

    private void logoutSuccessHandler(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        objectMapper.writeValue(response.getWriter(),  "Bye! See you Soon");
    }


    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider());
    }
}
