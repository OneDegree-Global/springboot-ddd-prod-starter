package hk.onedegree.domain.auth.services;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import hk.onedegree.domain.auth.aggregates.user.User;
import hk.onedegree.domain.auth.exceptions.InValidEmailException;
import hk.onedegree.domain.auth.exceptions.InValidPasswordException;
import hk.onedegree.domain.auth.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthorizationServiceTest {

    private AuthenticationService authenticationService;
    private ListAppender<ILoggingEvent> appender;
    private Logger appLogger = (Logger) LoggerFactory.getLogger(AuthenticationService.class);
    private Instant freezeInstant = Instant.ofEpochSecond(1617171335);
    @Mock private UserRepository mockUserRepository;

    @BeforeEach
    public void setup(){
        this.appender = new ListAppender<>();
        this.appender.start();
        this.appLogger.addAppender(this.appender);
        this.authenticationService = new AuthenticationService();
        this.authenticationService.clock = Clock.fixed(this.freezeInstant, ZoneId.of("UTC"));
        this.authenticationService.userRepository = this.mockUserRepository;

    }

    @AfterEach
    public void tearDown() {
        appLogger.detachAppender(appender);
    }

    @Test
    public void authenticate_InvalidJwtStr_LogErrorAndReturnEmpty() {
        String jwtStr = "whatever";

        Optional<User> result = this.authenticationService.authenticate(jwtStr);

        Assertions.assertEquals(true, result.isEmpty());
        assertThat(appender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .containsExactly("Parse jwt str error, exception: ");
    }

    @Test
    public void authenticate_JwtKidNotFound_LogErrorAndReturnEmpty() throws URISyntaxException, IOException, ParseException {
        URL resource = getClass().getClassLoader().getResource("rs256_jwks.json");
        File file = new File(resource.toURI());
        JWKSet jwkSet = JWKSet.load(file);
        this.authenticationService.jwkSource = new ImmutableJWKSet<>(jwkSet);

        String jwtStr = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjU1NjY5NzgifQ.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.SbacBNddGqV3yoaPQufpAl2EgyWJ8uoItsrvPd_2-2knbcJ_skfae99uzvmMI-3OTnxg5tqEnFqwL_i_dSE2X2X-nscL21swMdzKaG-n9sEiGwODCLsSiDbXa7iJbBV_Z8H_TP69GeP_5ni-SaVU6_WMDuMd6Nyu9dheVNPyFbQoWa1lxTSZhYhiuI3iT6Taj-tziX2A_3sDffoVtpRj-TeFVqP09Qc9n7ntzStAtw1rfuqEuB976MhOIcHxN-iwS00RQDlS60A0fSXb57SHDIpIv9e984PJjqpFcZJk1ZyYyVBmiMHMvdkZy5s8d6Gq7p4kxi-mE-l5k0fefeaFzw";

        Optional<User> result = this.authenticationService.authenticate(jwtStr);

        Assertions.assertEquals(true, result.isEmpty());
        assertThat(appender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .containsExactly("Get user from claim set in jwt fails, e: ");
    }

    @Test
    public void authenticate_UnsupportedJwtAlg_LogErrorAndReturnEmpty() throws URISyntaxException, IOException, ParseException {
        URL resource = getClass().getClassLoader().getResource("whatever_jwks.json");
        File file = new File(resource.toURI());
        JWKSet jwkSet = JWKSet.load(file);
        this.authenticationService.jwkSource = new ImmutableJWKSet<>(jwkSet);

        String jwtStr = "eyJraWQiOiIxIiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJzdWIiOiJ3aGF0ZXZlckB3aGF0ZXZlci5jb20iLCJpc3MiOiJjeW1ldHJpY3MiLCJleHAiOjE2MTcxNzE5MzUsImlhdCI6MTYxNzE3MTMzNX0.hfD2jnigBk3E16Y7NjvaAjfunhN1bZtGrycjZ-Bpt5s";

        Optional<User> result = this.authenticationService.authenticate(jwtStr);

        Assertions.assertEquals(true, result.isEmpty());
        assertThat(appender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .containsExactly("Get user from claim set in jwt fails, e: ");
    }

    @Test
    public void authenticate_JwtExpire_LogErrorAndReturnEmpty() throws URISyntaxException, IOException, ParseException, InValidEmailException {
        URL resource = getClass().getClassLoader().getResource("es256_jwks.json");
        File file = new File(resource.toURI());
        JWKSet jwkSet = JWKSet.load(file);
        this.authenticationService.jwkSource = new ImmutableJWKSet<>(jwkSet);

        String jwtStr = "eyJraWQiOiIxIiwidHlwIjoiSldUIiwiYWxnIjoiRVMyNTYifQ.eyJpc3MiOiJjeW1ldHJpY3MiLCJzdWIiOiJ3aGF0ZXZlciIsImV4cCI6MTYxMjE5NTIwMCwiaWF0IjoxNjEyMTA4ODAwfQ.UUemlt3h8rReQXl50jmAmGwqyH1EmzjvDAyqRtYminTFtSpd0hxzxxuHzD6tevOrqQr9K5j8fnPeS3MYSmhmvg";

        Optional<User> result = this.authenticationService.authenticate(jwtStr);

        Assertions.assertEquals(true, result.isEmpty());
        assertThat(appender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .containsExactly("Get user from claim set in jwt fails, e: ");
    }

    @Test
    public void authenticate_jwt() throws URISyntaxException, IOException, ParseException, InValidEmailException {
        URL resource = getClass().getClassLoader().getResource("es256_jwks.json");
        File file = new File(resource.toURI());
        JWKSet jwkSet = JWKSet.load(file);
        this.authenticationService.jwkSource = new ImmutableJWKSet<>(jwkSet);

        String email = "whatever@whatever.com";
        String id = "whatever";
        User fakeUser = new User(id, email);
        when(this.mockUserRepository.findById(eq(id))).thenReturn(Optional.of(fakeUser));

        String jwtStr = "eyJraWQiOiIxIiwidHlwIjoiSldUIiwiYWxnIjoiRVMyNTYifQ.eyJpc3MiOiJjeW1ldHJpY3MiLCJzdWIiOiJ3aGF0ZXZlciIsImV4cCI6MTYxNzI1NzczNSwiaWF0IjoxNjE3MTcxMzM1fQ.MjvsPjEinVHxd3A6dPfruAw9VDd4KMGbdokQ3b14DUndpMfqN7-EpZ8OxHVMUFAbxeDZxlif6W6AVf_sDUCS-Q";

        Optional<User> result = this.authenticationService.authenticate(jwtStr);

        Assertions.assertEquals(fakeUser.getId(), result.get().getId());
        Assertions.assertEquals(fakeUser.getEmail(), result.get().getEmail());
    }

    @Test
    public void authenticate_JwtInvalidSignature_LogErrorAndReturnEmpty() throws URISyntaxException, IOException, ParseException, InValidEmailException {
        URL resource = getClass().getClassLoader().getResource("rs256_jwks.json");
        File file = new File(resource.toURI());
        JWKSet jwkSet = JWKSet.load(file);
        this.authenticationService.jwkSource = new ImmutableJWKSet<>(jwkSet);
        //測試即時發的 token (時效內) 可以合法獲得 user，不用鎖死的 freezeInstant
        this.authenticationService.clock = Clock.systemUTC();

        String jwtStr = "eyJraWQiOiIxIiwiYWxnIjoiUlMyNTYiLCJ0eXAiOiJKV1QifQ.eyJzdWIiOiJ3aGF0ZXZlciIsImlzcyI6ImN5bWV0cmljcyIsImV4cCI6MTYxNzE3MTkzNSwiaWF0IjoxNjE3MTcxMzM1LCJlbWFpbCI6IndoYXRldmVyQHdoYXRldmVyLmNvbSJ9.AbvUcqEDuPWtsnU4zBcUtTloKkbKAhnDl3PS2v2X1WFrVwy4JAKndbFeIURaerEKJChOUXGNsq6E39uT59MGhvxCGktlSeres-g9-eo0SA1MgfNkidjPA9WEVgqb1awjjJUNHyVdW4KoaNbUYIEpvZ7ieiku-5l3ghCSlY_6tcpTZkCJ4xXAhZvvfTn0x8e74_TNeH7aIU6lnQImJt9HAQpTiKZvhXSzvniDwK7dv16FJREFvrl0xo8HBKexXZX9NmZwCrayDDeziIIDhsRTpHPVfouTO7kQ-c3YKrK5hueSCXyzHOOE65uU1mgRl3ZOmZ1lRLaxgO_hJWDT_Bt4Gw";

        Optional<User> result = this.authenticationService.authenticate(jwtStr);

        assertThat(appender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .containsExactly("Get user from claim set in jwt fails, e: ");

    }

    @Test
    public void authenticate_Password() throws URISyntaxException, IOException, ParseException, InValidEmailException, InValidPasswordException {
        String email = "whatever@whatever.com";
        String id = "whatever";
        String pwd = "Abc1234567";
        User fakeUser = new User(id, email);
        fakeUser.setPassword(pwd);

        when(this.mockUserRepository.findByEmail(eq(email))).thenReturn(Optional.of(fakeUser));

        Optional<User> result = this.authenticationService.authenticate(email, pwd);

        Assertions.assertEquals(email, result.get().getEmail());
        Assertions.assertEquals(id, result.get().getId());
    }

    @Test
    public void authenticate_InCorrectPassword_ReturnEmpty() throws URISyntaxException, IOException, ParseException, InValidEmailException, InValidPasswordException {
        String email = "whatever@whatever.com";
        String id = "whatever";
        String pwd = "Abc1234567";
        String incorrectPwd = "Abc7654321";
        User fakeUser = new User(id, email);
        fakeUser.setPassword(pwd);

        when(this.mockUserRepository.findByEmail(eq(email))).thenReturn(Optional.of(fakeUser));

        Optional<User> result = this.authenticationService.authenticate(email, incorrectPwd);

        Assertions.assertTrue(result.isEmpty());
    }
}
