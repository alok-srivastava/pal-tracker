package io.pivotal.pal.tracker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity

public class SecurityConfiguration extends WebSecurityConfigurerAdapter {


    boolean httpsDisabled;

    public  SecurityConfiguration(@Value("${https.disabled}") boolean httpsDisabled){
        this.httpsDisabled = httpsDisabled;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .inMemoryAuthentication()
                .withUser("user").password("password").roles("USER");
    }

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {

        if(!httpsDisabled) {
            httpSecurity.requiresChannel().anyRequest().requiresSecure();
        }

        httpSecurity.authorizeRequests().antMatchers("/**").hasRole("USER").and()
                .httpBasic().and().csrf().disable();


    }
}
