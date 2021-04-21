package hk.onedegree.web.springboot.config.di;

import hk.onedegree.domain.auth.repository.UserRepository;
import hk.onedegree.domain.auth.services.AuthenticationService;
import hk.onedegree.domain.auth.services.TokenService;
import hk.onedegree.domain.auth.services.UserAuthInfoService;
import hk.onedegree.persistence.mem.MemUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainService {

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
    public UserRepository userRepositoryBean(){
        MemUserRepository memUserRepository  = new MemUserRepository();
        return memUserRepository;
    }

    @Bean
    public UserAuthInfoService userAuthInfoServiceBean() {
        UserAuthInfoService userAuthInfoService = new UserAuthInfoService();
        return userAuthInfoService;
    }
}
