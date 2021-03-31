package hk.onedegree.domain.auth.services;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import ch.qos.logback.classic.Logger;
import com.nimbusds.jwt.SignedJWT;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.provider.CsvSource;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.setPrintAssertionsDescription;


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
    @CsvSource({
            "es256_jwks.json,eyJraWQiOiIxIiwidHlwIjoiSldUIiwiYWxnIjoiRVMyNTYifQ.eyJzdWIiOiJ3aGF0ZXZlckB3aGF0ZXZlci5jb20iLCJpc3MiOiJjeW1ldHJpY3MiLCJleHAiOjE2MTcxNzE5MzUsImlhdCI6MTYxNzE3MTMzNX0.eBCh2y4dVTobki5Pa950m8x-cgPlyc7s2vbtavLM7mtpLZq60cQcgP6R1N60M3Vy5v7ZjdZyiqTu_7mKSqNHVQ", //too short: 8-30
            "rs256_jwks.json,eyJraWQiOiIxIiwidHlwIjoiSldUIiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiJ3aGF0ZXZlckB3aGF0ZXZlci5jb20iLCJpc3MiOiJjeW1ldHJpY3MiLCJleHAiOjE2MTcxNzE5MzUsImlhdCI6MTYxNzE3MTMzNX0.A1TNhmwE4hxoVgzBD-jH1YgpE4xkOAM_lQHuiPi0Q6PKc8gYa4OSFc0geKD1CPnln30fsewv4mofQvtu7ESwRp2QtdC9UX5rkcdRKf2AqzQV-YID_qxPEpIei-M5VG4kqgMBGcTedT6Ysn6V3IKOTfhMyb1r-YDUnuAyM8f3ZgBw08YQKsBP_v6MNsK0_C1iAzIxLsPXFOG-RZ5exjxpJARy6S59deqXuLJH44t5BM3tnapW0zGZlTZdt8C_nnSoWaWAVn81kRTZHPucSLVH-E3O5dpjBfMItmHh4fAPAcvFQF-CQZLDngCDuPC-aLoQMeaW6uYnBI_-wk9O_Rp5LioqKws_UkJ4ioT3hP9sciMtwfEx-GVpQ6e2MrfoC-qtfTA_GCgIVrB6BqcA9T9cWHoacbFLlQ_uc3EIBrtHESNcAVWllrVJ4-RRERt362kPaJXifE8r8hdpgb9h9qIQjYd5qpPE5DhpXANpDzidhnENQ40KbyR-t0sueO9TorI5BLDneQjcg5NMkFjBLoV2O60DFlC2EzG2TIERkpf-xhqqTN3nPKIv-X-_as2dx0ShMYpDQeoQTbp4gKzbh94HmTxM8lQxf9lMLvyESR9lt2vqTxG2zQRU2iuwMpbqSeu6qwF5orcH9bllbmIdtRpV3CPEGiVNozchhFEPngl7DW0"
    })
    public void issueToken(String jwkfile, String expect) throws URISyntaxException, IOException, ParseException, InValidEmailException, JSONException {
        URL resource = getClass().getClassLoader().getResource(jwkfile);
        File file = new File(resource.toURI());
        JWKSet jwkSet = JWKSet.load(file);
        this.authTokenService.jwkSet = jwkSet;

        String email = "whatever@whatever.com";
        String id = "whatever";
        User fakeUser = new User(id, email);

        String result = this.authTokenService.issueJWT(fakeUser);
        SignedJWT actualJwt = SignedJWT.parse(result);
        SignedJWT expectJwt = SignedJWT.parse(expect);

        JSONAssert.assertEquals(
                actualJwt.getHeader().toString(),
                expectJwt.getHeader().toString(),
                JSONCompareMode.STRICT);

        JSONAssert.assertEquals(
                actualJwt.getJWTClaimsSet().toString(),
                expectJwt.getJWTClaimsSet().toString(),
                JSONCompareMode.STRICT);
    }

    @Test
    public void issueToken_UnsupportedAlg_LogError() throws URISyntaxException, IOException, ParseException, InValidEmailException {
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
    public void validateJWT_NonJwtStr_LogError() {
        String jwtStr = "whatever";

        boolean result = this.authTokenService.validateJWT(jwtStr);

        Assertions.assertEquals(false, result);
        assertThat(appender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .containsExactly("Parse jwt str error, exception: ");
    }

    @Test
    public void validateJWT_KidNotFound_LogError() throws URISyntaxException, IOException, ParseException {
        URL resource = getClass().getClassLoader().getResource("rs256_jwks.json");
        File file = new File(resource.toURI());
        JWKSet jwkSet = JWKSet.load(file);
        this.authTokenService.jwkSet = jwkSet;

        String jwtStr = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjU1NjY5NzgifQ.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.SbacBNddGqV3yoaPQufpAl2EgyWJ8uoItsrvPd_2-2knbcJ_skfae99uzvmMI-3OTnxg5tqEnFqwL_i_dSE2X2X-nscL21swMdzKaG-n9sEiGwODCLsSiDbXa7iJbBV_Z8H_TP69GeP_5ni-SaVU6_WMDuMd6Nyu9dheVNPyFbQoWa1lxTSZhYhiuI3iT6Taj-tziX2A_3sDffoVtpRj-TeFVqP09Qc9n7ntzStAtw1rfuqEuB976MhOIcHxN-iwS00RQDlS60A0fSXb57SHDIpIv9e984PJjqpFcZJk1ZyYyVBmiMHMvdkZy5s8d6Gq7p4kxi-mE-l5k0fefeaFzw";

        boolean result = this.authTokenService.validateJWT(jwtStr);

        Assertions.assertEquals(false, result);
        assertThat(appender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .containsExactly("Kid 5566978 not found.");
    }
}
