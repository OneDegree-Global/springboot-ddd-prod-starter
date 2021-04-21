package hk.onedegree.web.springboot.config.di;


import hk.onedegree.application.aspect.AuthorizeAspect;
import hk.onedegree.application.services.LoginService;
import hk.onedegree.application.services.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ApplicationService {

    @Bean
    public AuthorizeAspect authorizeAspectBean(){
        AuthorizeAspect authorizeAspect = new AuthorizeAspect();
        return authorizeAspect;
    }


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
}