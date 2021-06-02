package com.cymetrics.web.springboot.config.di;


import com.cymetrics.application.aspect.AuthorizeAspect;
import com.cymetrics.application.services.LoginService;
import com.cymetrics.application.services.UserService;
import com.cymetrics.domain.scheduling.services.ScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ApplicationService {

    private static Logger logger = LoggerFactory.getLogger(ScheduleService.class);

    @Bean
    public AuthorizeAspect authorizeAspectBean() {
        AuthorizeAspect authorizeAspect = new AuthorizeAspect();
        return authorizeAspect;
    }


    @Bean
    public UserService userServiceBean() {
        UserService userService = new UserService();
        return userService;
    }

    @Bean
    @ConditionalOnExpression(
            " '${launch.type}'.equals('web') "
    )
    public ScheduleService scheduleServiceBean() {
        ScheduleService scheduleService = new ScheduleService();
        return scheduleService;
    }

    @Bean
    public LoginService loginServiceBean() {
        LoginService loginService = new LoginService();
        return loginService;
    }


}