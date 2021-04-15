package hk.onedegree.web.springboot.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import hk.onedegree.application.aspect.AuthorizeAspect;
import hk.onedegree.application.helper.AuthHelper;
import hk.onedegree.application.services.LoginService;
import hk.onedegree.application.services.UserService;
import hk.onedegree.domain.auth.services.AuthenticationService;
import hk.onedegree.domain.auth.services.TokenService;
import hk.onedegree.domain.auth.services.UserAuthInfoService;
import hk.onedegree.persistence.mem.MemUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Named;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;

@Configuration
public class DiConfig {

    @Bean
    public UserService userServiceBean() {
        UserService userService = new UserService();
        return userService;
    }

    @Bean
    public LoginService loginServiceBean(){
        LoginService loginService = new LoginService();
        return loginService;
    }

    @Bean
    public AuthenticationService authenticationServiceBean(){
        AuthenticationService authenticationServiceBean = new AuthenticationService();
        return authenticationServiceBean;
    }

    @Bean
    public TokenService tokenServiceBean(){
        TokenService tokenService  = new TokenService();
        return tokenService;
    }

    @Bean
    @Named("Memory")
    public MemUserRepository memUserRepositoryBean(){
        MemUserRepository memUserRepository  = new MemUserRepository();
        return memUserRepository;
    }

    @Bean
    public JWKSet jwkSetBean() throws URISyntaxException, IOException, ParseException {
        URL resource = getClass().getClassLoader().getResource("jwkset.json");
        File file = new File(resource.toURI());
        JWKSet jwkSet = JWKSet.load(file);
        return jwkSet;
    }

    @Bean
    public JWKSource jwkSourceBean() throws URISyntaxException, IOException, ParseException {
        URL resource = getClass().getClassLoader().getResource("jwkset.json");
        File file = new File(resource.toURI());
        JWKSet jwkSet = JWKSet.load(file);
        return new ImmutableJWKSet<>(jwkSet);
    }

    @Bean
    public UserAuthInfoService userAuthInfoServiceBean() {
        UserAuthInfoService userAuthInfoService = new UserAuthInfoService();
        return userAuthInfoService;
    }

    @Bean
    public AuthorizeAspect authorizeAspectBean(){
        AuthorizeAspect authorizeAspect = new AuthorizeAspect();
        return authorizeAspect;
    }

    @Bean
    public AuthHelper authHelper(){
        AuthHelper authHelper = new AuthHelper();
        return authHelper;
    }
}
