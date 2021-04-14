package hk.onedegree.web.springboot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

//@Configuration
public class SecurityConf extends WebSecurityConfigurerAdapter {
    protected void configure(HttpSecurity http) throws Exception {
//        http.headers();
//                .xssProtection();
//                .and()
//                .contentSecurityPolicy("script-src 'self'");
    }
}
