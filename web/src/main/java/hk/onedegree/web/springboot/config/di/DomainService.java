package hk.onedegree.web.springboot.config.di;

import hk.onedegree.domain.auth.repository.UserRepository;
import hk.onedegree.domain.auth.services.AuthenticationService;
import hk.onedegree.domain.auth.services.TokenService;
import hk.onedegree.domain.auth.services.UserAuthInfoService;
import hk.onedegree.persistence.mem.MemUserRepository;
import hk.onedegree.persistence.rdbms.RdbmsUserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
    @ConditionalOnProperty(
            value="server.persistence.type",
            havingValue = "memory",
            matchIfMissing = false)
    public UserRepository memUserRepositoryBean (){
        MemUserRepository memUserRepository  = new MemUserRepository();
        return memUserRepository;
    }

    @Bean
    @ConditionalOnProperty(
            value="server.persistence.type",
            havingValue = "rdbms",
            matchIfMissing = false)
    public UserRepository rdbmsUserRepositoryBean (){
        RdbmsUserRepository rdbmsUserRepository = new RdbmsUserRepository();
        return rdbmsUserRepository;
    }

    @Bean
    public UserAuthInfoService userAuthInfoServiceBean() {
        UserAuthInfoService userAuthInfoService = new UserAuthInfoService();
        return userAuthInfoService;
    }
}
