package org.alexburchak.trice.config;

import lombok.extern.slf4j.Slf4j;
import org.alexburchak.trice.controller.HookController;
import org.alexburchak.trice.controller.TriceController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * @author alexburchak
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private TriceConfiguration triceConfiguration;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .exceptionHandling().and()
                .headers().cacheControl().and()
                .and()
                .headers().frameOptions().disable().and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .csrf().disable().authorizeRequests()
                .antMatchers("/*.js").permitAll()
                .antMatchers("/*.png").permitAll()
                .antMatchers("/*.txt").permitAll()
                .antMatchers("/*.css").permitAll()
                .antMatchers(TriceController.PATH_TRICE).permitAll()
                .antMatchers(HookController.PATH_HOOK).permitAll()
                .antMatchers(triceConfiguration.getEndpoint() + "/**").permitAll()
                .antMatchers("/**").authenticated();
    }
}
