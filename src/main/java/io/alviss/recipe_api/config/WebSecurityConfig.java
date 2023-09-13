package io.alviss.recipe_api.config;


import io.alviss.recipe_api.config.jwt.AuthEntryPointJwt;
import io.alviss.recipe_api.config.jwt.AuthTokenFilter;
import io.alviss.recipe_api.user.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true
)
@RequiredArgsConstructor
public class WebSecurityConfig {


    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final AuthEntryPointJwt unauthorizedHandler;

    private static final RequestMatcher PUBLIC_URL = new OrRequestMatcher(
            new AntPathRequestMatcher("/api/v1/auth/**", "POST", true),
            new AntPathRequestMatcher(
                    "/api/v1/test/**"
            ),
            new AntPathRequestMatcher("/error"),
            new AntPathRequestMatcher("/api/v1/ping")

    );

    @Bean
    public AuthTokenFilter authTokenFilter () {
        return new AuthTokenFilter();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer () {
        return web -> web.ignoring().requestMatchers(PUBLIC_URL);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider () {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setPasswordEncoder(passwordEncoder);
        authProvider.setUserDetailsService((UserDetailsManager) userService);
        authProvider.setUserDetailsPasswordService((UserDetailsPasswordService) userService);

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager (AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain (HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests().antMatchers("/api/v1/auth/**").permitAll()
                .antMatchers("/api/v1/hello").permitAll()
                .antMatchers("/api/v1/ping").permitAll()
                .antMatchers("/error").permitAll()
                .antMatchers("/api/v1/test/**").permitAll()
                .anyRequest().authenticated();

        httpSecurity.authenticationProvider(authenticationProvider());

        httpSecurity.addFilterBefore(authTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}
