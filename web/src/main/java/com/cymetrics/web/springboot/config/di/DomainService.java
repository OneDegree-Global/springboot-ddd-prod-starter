package com.cymetrics.web.springboot.config.di;

import com.cymetrics.domain.auth.repository.UserRepository;
import com.cymetrics.domain.auth.services.AuthenticationService;
import com.cymetrics.domain.auth.services.TokenService;
import com.cymetrics.domain.auth.services.UserAuthInfoService;
import com.cymetrics.domain.scheduling.repository.ScheduleRepository;
import com.cymetrics.persistence.mem.MemUserRepository;
import com.cymetrics.persistence.rdbms.RdbmsScheduleRepository;
import com.cymetrics.persistence.rdbms.RdbmsUserRepository;
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

    @Bean
    @ConditionalOnProperty(
            value="server.persistence.type",
            havingValue = "rdbms",
            matchIfMissing = false)
    public ScheduleRepository rdbmsScheduleRepositoryBean (){
        RdbmsScheduleRepository rdbmsScheduleRepository = new RdbmsScheduleRepository();
        return rdbmsScheduleRepository;
    }
}
