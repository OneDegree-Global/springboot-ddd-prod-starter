package hk.onedegree.web.springboot.config.di;

import hk.onedegree.domain.auth.repository.UserRepository;
import hk.onedegree.domain.auth.services.AuthenticationService;
import hk.onedegree.domain.auth.services.TokenService;
import hk.onedegree.domain.auth.services.UserAuthInfoService;
import hk.onedegree.persistence.mem.MemUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class DomainService {

    private static Logger logger = LoggerFactory.getLogger(DomainService.class);

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

    public enum PERSISTENCE_TYPE {
        MEMORY,
    }

    @Value("${server.persistence.type}")
    private String persistenceType;

    @Bean
    public UserRepository userRepositoryBean(){
        switch (PERSISTENCE_TYPE.valueOf(persistenceType.toUpperCase())) {
            case MEMORY:
                MemUserRepository memUserRepository  = new MemUserRepository();
                return memUserRepository;
            default:
                logger.error("Invelid persistence type");
                return null;
        }
    }

    @Bean
    public UserAuthInfoService userAuthInfoServiceBean() {
        UserAuthInfoService userAuthInfoService = new UserAuthInfoService();
        return userAuthInfoService;
    }
}
