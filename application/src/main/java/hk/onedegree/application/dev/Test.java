package hk.onedegree.application.dev;

import hk.onedegree.application.services.OtherService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan
public class Test {

    public static void main(String[] args) {
        OtherService otherService = new OtherService();
        String token = "whatever token";
        otherService.doWhateverThing(token);
    }
}
