package hk.onedegree.domain.auth.services;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import hk.onedegree.domain.auth.aggregates.user.User;
import hk.onedegree.domain.auth.constant.Constant;
import hk.onedegree.domain.auth.exceptions.InValidEmailException;
import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class TokenServiceTest {

    private TokenService tokenService;
    private ListAppender<ILoggingEvent> appender;
    private Logger appLogger = (Logger) LoggerFactory.getLogger(TokenService.class);
    private Instant freezeInstant = Instant.ofEpochSecond(1617171335);

    @BeforeEach
    public void setup(){
        appender = new ListAppender<>();
        appender.start();
        appLogger.addAppender(appender);
        tokenService = new TokenService();
        tokenService.clock = Clock.fixed(freezeInstant, ZoneId.of("UTC"));
    }

    @AfterEach
    public void tearDown() {
        appLogger.detachAppender(appender);
    }

    // 用 https://jwt.io/ 產生測試用 token
    @ParameterizedTest
    @ValueSource(strings = {"es256_jwks.json", "rs256_jwks.json"})
    public void issueToken(String jwkfile) throws URISyntaxException, IOException, ParseException, InValidEmailException, JSONException {
        URL resource = getClass().getClassLoader().getResource(jwkfile);
        File file = new File(resource.toURI());
        JWKSet jwkSet = JWKSet.load(file);
        this.tokenService.jwkSet = jwkSet;

        String email = "whatever@whatever.com";
        String id = "whatever";
        User fakeUser = new User(id, email);

        Optional<String> result = this.tokenService.issueToken(Optional.of(fakeUser));
        SignedJWT actualJwt = SignedJWT.parse(result.get());

        JWSHeader expectHeader = prepareJWSHeader(jwkSet);
        JWTClaimsSet expectClaimsSet = prepareJWTClaimsSet(jwkSet, fakeUser);

        JSONAssert.assertEquals(
                actualJwt.getHeader().toString(),
                expectHeader.toString(),
                JSONCompareMode.STRICT);

        JSONAssert.assertEquals(
                actualJwt.getJWTClaimsSet().toString(),
                expectClaimsSet.toString(),
                JSONCompareMode.STRICT);

    }

    private JWTClaimsSet prepareJWTClaimsSet(JWKSet jwkSet, User user){
        long iat = freezeInstant.toEpochMilli();
        long exp = freezeInstant.plus(1, ChronoUnit.DAYS).toEpochMilli();
        JWTClaimsSet expectClaimsSet = new JWTClaimsSet.Builder()
                .issuer(Constant.issuer)
                .issueTime(new Date(iat))
                .expirationTime(new Date(exp))
                .subject(user.getId())
                .build();

        return expectClaimsSet;
    }

    private JWSHeader prepareJWSHeader(JWKSet jwkSet){
        String alg = jwkSet.getKeys().get(0).getAlgorithm().toString();
        String kid = jwkSet.getKeys().get(0).getKeyID();
        JWSAlgorithm jwsAlgorithm = JWSAlgorithm.parse(alg);
        JWSHeader expectHeader = new JWSHeader.Builder(jwsAlgorithm)
                .type(JOSEObjectType.JWT)
                .keyID(kid)
                .build();

        return expectHeader;
    }

    @Test
    public void issueToken_UnsupportedAlg_LogErrorAndReturnFalse() throws URISyntaxException, IOException, ParseException, InValidEmailException {
        URL resource = getClass().getClassLoader().getResource("whatever_jwks.json");
        File file = new File(resource.toURI());
        JWKSet jwkSet = JWKSet.load(file);
        this.tokenService.jwkSet = jwkSet;

        String email = "whatever@whatever.com";
        String id = "whatever";
        User fakeUser = new User(id, email);

        Optional<String> result = this.tokenService.issueToken(Optional.of(fakeUser));

        Assertions.assertEquals("", result.get());
        assertThat(appender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .containsExactly("Unsupported alg in jwk: hs256");
    }

    @Test
    public void issueToken_EmptyUser_ReturnEmptyStr() {

        Optional<String> result = this.tokenService.issueToken(Optional.empty());

        Assertions.assertEquals("", result.get());
    }
}
