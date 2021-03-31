package hk.onedegree.domain.auth.services;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import ch.qos.logback.classic.Logger;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.slf4j.LoggerFactory;
import com.nimbusds.jose.jwk.JWKSet;
import hk.onedegree.domain.auth.aggregates.user.User;
import hk.onedegree.domain.auth.exceptions.InValidEmailException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

// 這裡測試用的 token 從 https://jwt.io/ 產生

public class AuthTokenServiceTest {

    private AuthTokenService authTokenService;
    private ListAppender<ILoggingEvent> appender;
    private Logger appLogger = (Logger) LoggerFactory.getLogger(AuthTokenService.class);
    private Instant freezeInstant = Instant.ofEpochSecond(1617171335);

    @BeforeEach
    public void setup(){
        appender = new ListAppender<>();
        appender.start();
        appLogger.addAppender(appender);
        authTokenService = new AuthTokenService();
        authTokenService.clock = Clock.fixed(freezeInstant, ZoneId.of("UTC"));
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
        this.authTokenService.jwkSet = jwkSet;

        String email = "whatever@whatever.com";
        String id = "whatever";
        User fakeUser = new User(id, email);

        String result = this.authTokenService.issueJWT(fakeUser);
        SignedJWT actualJwt = SignedJWT.parse(result);

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

        System.out.println(expectClaimsSet.toString());
    }

    private JWTClaimsSet prepareJWTClaimsSet(JWKSet jwkSet, User user){
        long iat = freezeInstant.toEpochMilli();
        long exp = freezeInstant.plus(10, ChronoUnit.MINUTES).toEpochMilli();
        JWTClaimsSet expectClaimsSet = new JWTClaimsSet.Builder()
                .issuer("cymetrics")
                .issueTime(new Date(iat))
                .expirationTime(new Date(exp))
                .subject(user.getId())
                .claim("email", user.getEmail())
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
        this.authTokenService.jwkSet = jwkSet;

        String email = "whatever@whatever.com";
        String id = "whatever";
        User fakeUser = new User(id, email);

        String result = this.authTokenService.issueJWT(fakeUser);

        Assertions.assertEquals("", result);
        assertThat(appender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .containsExactly("Unsupported alg in jwk: hs256");
    }

    @Test
    public void getUserFromJWT_NonJwtStr_LogErrorAndReturnEmpty() {
        String jwtStr = "whatever";

        Optional<User> result = this.authTokenService.getUserFromJWT(jwtStr);

        Assertions.assertEquals(true, result.isEmpty());
        assertThat(appender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .containsExactly("Parse jwt str error, exception: ");
    }

    @Test
    public void getUserFromJWT_KidNotFound_LogErrorAndReturnEmpty() throws URISyntaxException, IOException, ParseException {
        URL resource = getClass().getClassLoader().getResource("rs256_jwks.json");
        File file = new File(resource.toURI());
        JWKSet jwkSet = JWKSet.load(file);
        this.authTokenService.jwkSet = jwkSet;
        this.authTokenService.jwkSource = new ImmutableJWKSet<>(jwkSet);

        String jwtStr = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjU1NjY5NzgifQ.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.SbacBNddGqV3yoaPQufpAl2EgyWJ8uoItsrvPd_2-2knbcJ_skfae99uzvmMI-3OTnxg5tqEnFqwL_i_dSE2X2X-nscL21swMdzKaG-n9sEiGwODCLsSiDbXa7iJbBV_Z8H_TP69GeP_5ni-SaVU6_WMDuMd6Nyu9dheVNPyFbQoWa1lxTSZhYhiuI3iT6Taj-tziX2A_3sDffoVtpRj-TeFVqP09Qc9n7ntzStAtw1rfuqEuB976MhOIcHxN-iwS00RQDlS60A0fSXb57SHDIpIv9e984PJjqpFcZJk1ZyYyVBmiMHMvdkZy5s8d6Gq7p4kxi-mE-l5k0fefeaFzw";

        Optional<User> result = this.authTokenService.getUserFromJWT(jwtStr);

        Assertions.assertEquals(true, result.isEmpty());
        assertThat(appender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .containsExactly("Get user from claim set in jwt fails, e: ");
    }

    @Test
    public void getUserFromJWT_UnsupportedAlg_LogErrorAndReturnEmpty() throws URISyntaxException, IOException, ParseException {
        URL resource = getClass().getClassLoader().getResource("whatever_jwks.json");
        File file = new File(resource.toURI());
        JWKSet jwkSet = JWKSet.load(file);
        this.authTokenService.jwkSet = jwkSet;
        this.authTokenService.jwkSource = new ImmutableJWKSet<>(jwkSet);

        String jwtStr = "eyJraWQiOiIxIiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJzdWIiOiJ3aGF0ZXZlckB3aGF0ZXZlci5jb20iLCJpc3MiOiJjeW1ldHJpY3MiLCJleHAiOjE2MTcxNzE5MzUsImlhdCI6MTYxNzE3MTMzNX0.hfD2jnigBk3E16Y7NjvaAjfunhN1bZtGrycjZ-Bpt5s";

        Optional<User> result = this.authTokenService.getUserFromJWT(jwtStr);

        Assertions.assertEquals(true, result.isEmpty());
        assertThat(appender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .containsExactly("Get user from claim set in jwt fails, e: ");
    }

    @Test
    public void getUserFromJWT_Expire_LogErrorAndReturnEmpty() throws URISyntaxException, IOException, ParseException, InValidEmailException {
        URL resource = getClass().getClassLoader().getResource("rs256_jwks.json");
        File file = new File(resource.toURI());
        JWKSet jwkSet = JWKSet.load(file);
        this.authTokenService.jwkSet = jwkSet;
        this.authTokenService.jwkSource = new ImmutableJWKSet<>(jwkSet);

        String jwtStr = "eyJraWQiOiIxIiwidHlwIjoiSldUIiwiYWxnIjoiUlMyNTYifQ.eyJpc3MiOiJjeW1ldHJpY3MiLCJzdWIiOiJ3aGF0ZXZlckB3aGF0ZXZlci5jb20iLCJleHAiOjE2MTcwMzQyMDAsImlhdCI6MTYxNzAzMzYwMH0.HqRlGjKizlPrLzD91WvS7-etZrfISrTwUWv48G-VWfWMx0eNHEeOe5YrflcFYpxQH4AUUHeIftOfve8UYVVfZCziiM4jTOSWAfAtNAsyHeJqZNrnfIdB_9sRbMzRcjf65TUeVZdM9GrhhsKr4jKFIffryEsSnrdwjukInzMN6hNt0k8byhowGFUYxogC8Ms2LRu3HkfauFvtb_oEBMu63kGQjgKoMBhyp03r_frMlY5sVbu1NnwCHdz-5i1e79mzZCGaKDK57AJNcx2vTfWTi9IcDMrjK4p_4Qi7rsYL30vFWlKaz755wc7wWhB6PabcM8fj2bNC3ZavuNuAIzsJOKVoKPEkmDfHvnz4PD9vpkMdG_PpltU2Sqz44PAMSb1DYZOaWtPNXFJ9Sa580lvhu6Eu37zlZF4OqzV3zzggvYvFO--4YzUtZ0DkyFWYcvVLZxUGjzn6uZWcBzHsM_c7U3N__6KP0YAUkbzntwqlGdtpaMGWf6TmpupOSL7_nNABbpqnQXOHLzF831PgHaTB5T5RtD0dNJYfyaWFSS7qUAozSqIjNEBuN9Q3s4dH8lLzD0BWrbY-n4OMIOBo57MEukvPSXu9r1xBmzs-jq4NYgctB-6FLt0M8zDPUdEuK2qNwTd9GuINRJuv3P5HzBGNMCGSD333dYhknwc9-1slwts";

        Optional<User> result = this.authTokenService.getUserFromJWT(jwtStr);

        Assertions.assertEquals(true, result.isEmpty());
        assertThat(appender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .containsExactly("Get user from claim set in jwt fails, e: ");
    }

    @Test
    public void getUserFromJWT() throws URISyntaxException, IOException, ParseException, InValidEmailException {
        URL resource = getClass().getClassLoader().getResource("rs256_jwks.json");
        File file = new File(resource.toURI());
        JWKSet jwkSet = JWKSet.load(file);
        this.authTokenService.jwkSet = jwkSet;
        this.authTokenService.jwkSource = new ImmutableJWKSet<>(jwkSet);
        //測試即時發的 token (時效內) 可以合法獲得 user，不用鎖死的 freezeInstant
        this.authTokenService.clock = Clock.systemUTC();

        String email = "whatever@whatever.com";
        String id = "whatever";
        User fakeUser = new User(id, email);
        String jwtStr = this.authTokenService.issueJWT(fakeUser);

        Optional<User> result = this.authTokenService.getUserFromJWT(jwtStr);

        Assertions.assertEquals(fakeUser.getId(), result.get().getId());
        Assertions.assertEquals(fakeUser.getEmail(), result.get().getEmail());
    }

    @Test
    public void getUserFromJWT_InvalidSignature_LogErrorAndReturnEmpty() throws URISyntaxException, IOException, ParseException, InValidEmailException {
        URL resource = getClass().getClassLoader().getResource("rs256_jwks.json");
        File file = new File(resource.toURI());
        JWKSet jwkSet = JWKSet.load(file);
        this.authTokenService.jwkSet = jwkSet;
        this.authTokenService.jwkSource = new ImmutableJWKSet<>(jwkSet);
        //測試即時發的 token (時效內) 可以合法獲得 user，不用鎖死的 freezeInstant
        this.authTokenService.clock = Clock.systemUTC();

        String jwtStr = "eyJraWQiOiIxIiwiYWxnIjoiUlMyNTYiLCJ0eXAiOiJKV1QifQ.eyJzdWIiOiJ3aGF0ZXZlciIsImlzcyI6ImN5bWV0cmljcyIsImV4cCI6MTYxNzE3MTkzNSwiaWF0IjoxNjE3MTcxMzM1LCJlbWFpbCI6IndoYXRldmVyQHdoYXRldmVyLmNvbSJ9.AbvUcqEDuPWtsnU4zBcUtTloKkbKAhnDl3PS2v2X1WFrVwy4JAKndbFeIURaerEKJChOUXGNsq6E39uT59MGhvxCGktlSeres-g9-eo0SA1MgfNkidjPA9WEVgqb1awjjJUNHyVdW4KoaNbUYIEpvZ7ieiku-5l3ghCSlY_6tcpTZkCJ4xXAhZvvfTn0x8e74_TNeH7aIU6lnQImJt9HAQpTiKZvhXSzvniDwK7dv16FJREFvrl0xo8HBKexXZX9NmZwCrayDDeziIIDhsRTpHPVfouTO7kQ-c3YKrK5hueSCXyzHOOE65uU1mgRl3ZOmZ1lRLaxgO_hJWDT_Bt4Gw";

        Optional<User> result = this.authTokenService.getUserFromJWT(jwtStr);

        assertThat(appender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .containsExactly("Get user from claim set in jwt fails, e: ");

    }
}
