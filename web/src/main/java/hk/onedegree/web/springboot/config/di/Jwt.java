package hk.onedegree.web.springboot.config.di;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;

@Configuration
public class Jwt {

    @Bean
    public JWKSource jwkSourceBean() throws URISyntaxException, IOException, ParseException {
        URL resource = getClass().getClassLoader().getResource("jwkset.json");
        File file = new File(resource.toURI());
        JWKSet jwkSet = JWKSet.load(file);
        return new ImmutableJWKSet<>(jwkSet);
    }

    @Bean
    public JWKSet jwkSetBean() throws URISyntaxException, IOException, ParseException {
        URL resource = getClass().getClassLoader().getResource("jwkset.json");
        File file = new File(resource.toURI());
        JWKSet jwkSet = JWKSet.load(file);
        return jwkSet;
    }
}
