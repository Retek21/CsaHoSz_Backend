package com.bme.aut.CsaHoSz.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    //@Autowired
    //private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    private UserDetailsService userDetailsService;


    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        OrgAuthenticationFilter  orgAuthenticationFilter = new OrgAuthenticationFilter(authenticationManagerBean());
        orgAuthenticationFilter.setFilterProcessesUrl("/api/login");
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(STATELESS);

        //User Controller Authorities
        http.authorizeRequests().antMatchers("/api/user/add_member").permitAll().and()
            .authorizeRequests().antMatchers("/api/user/add_coach").hasAnyAuthority("ROLE_ADMIN", "ROLE_COACH").and()
            .authorizeRequests().antMatchers("/api/user/add_admin").hasAnyAuthority("ROLE_ADMIN").and()
            .authorizeRequests().antMatchers("/api/user/update_role/**").hasAnyAuthority("ROLE_ADMIN").and()

        //Training Controller Authorities
            .authorizeRequests().antMatchers("/api/training/add_training").hasAnyAuthority("ROLE_ADMIN", "ROLE_COACH").and()
            .authorizeRequests().antMatchers("/api/training/remove_training").hasAnyAuthority("ROLE_ADMIN", "ROLE_COACH").and()

        //Message Controller Authorities

        //Cost Controller Authorities
            .authorizeRequests().antMatchers("/api/cost/add_training").hasAnyAuthority("ROLE_ADMIN", "ROLE_COACH").and()
            .authorizeRequests().antMatchers("/api/cost/remove_training").hasAnyAuthority("ROLE_ADMIN", "ROLE_COACH").and()


        //Competition Controller Authorities
            .authorizeRequests().antMatchers("/api/competition/new_competition").hasAnyAuthority("ROLE_ADMIN", "ROLE_COACH").and()
            .authorizeRequests().antMatchers("/api/competition/remove_competition").hasAnyAuthority("ROLE_ADMIN", "ROLE_COACH").and()
            .authorizeRequests().antMatchers("/api/competition/update/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_COACH").and()


        //Login Controller Authorities
        .authorizeRequests().antMatchers("/api/login/token_expiration_check").permitAll();

        http.authorizeRequests().anyRequest().authenticated();

        http.addFilter(orgAuthenticationFilter);
        http.addFilterBefore(new OrgAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }


    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
