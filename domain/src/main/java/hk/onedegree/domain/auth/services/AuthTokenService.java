package hk.onedegree.domain.auth.services;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import hk.onedegree.domain.auth.aggregates.user.User;
import hk.onedegree.domain.auth.exceptions.UnsupportedAlgException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.text.ParseException;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Date;

class AuthTokenService {

    @Inject
    JWKSet jwkSet;

    Clock clock = Clock.systemUTC();

    private static Logger logger = LoggerFactory.getLogger(AuthTokenService.class);

    final private String issuer = "cymetrics";

    public boolean isJwtExist(String jwtStr) {

        //TODO: 判斷 jwt token 是否還存在
        //ex. 用戶登出應該刪除所持有的 jwt，此時即使驗證合法的 jwt 也可能早就不在
        return false;
    }

    public boolean validateJWT(String jwtStr) {
        SignedJWT signedJWT = null;
        try {
            signedJWT = SignedJWT.parse(jwtStr);
        } catch (ParseException e) {
            logger.error("Parse jwt str error, exception: ", e);
            return false;
        }

        JWK jwk = this.jwkSet.getKeyByKeyId(signedJWT.getHeader().getKeyID());
        if (jwk==null) {
            logger.error("Kid {} not found.", signedJWT.getHeader().getKeyID());
            return false;
        }

        JWSVerifier verifier = null;
        try {
            verifier = getJWSVerifierByJwk(jwk);
        } catch (JOSEException e) {
            logger.error("Get jwt verifier error, exception: ", e);
            return false;
        } catch (UnsupportedAlgException e) {
            logger.error("Unsupported alg in jwk: {}", jwk.getAlgorithm().toString());
            return false;
        }

        try {
            return signedJWT.verify(verifier);
        } catch (JOSEException e) {
            logger.error("Verifier jwt error, exception: ", e);
            return false;
        }
    }

    public String issueJWT(User user) {

        JWK latestJwk = this.jwkSet.getKeys().get(0);
        String alg = latestJwk.getAlgorithm().toString();

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.parse(alg))
                .type(JOSEObjectType.JWT)
                .keyID(latestJwk.getKeyID())
                .build();


        Instant curInstant = clock.instant();
        long iat = curInstant.toEpochMilli();
        long exp = curInstant.plus(10, ChronoUnit.MINUTES).toEpochMilli();

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .issuer(issuer)
                .issueTime(new Date(iat))
                .expirationTime(new Date(exp))
                .subject(user.getEmail())
                .build();

        SignedJWT jwt = new SignedJWT(header, claimsSet);

        try {
            jwt.sign(getSignerByJwk(latestJwk));
        } catch (JOSEException e) {
            logger.error("Sign jwt error, exception: ", e);
            return "";
        } catch (UnsupportedAlgException e) {
            logger.error("Unsupported alg in jwk: {}", latestJwk.getAlgorithm().toString());
            return "";
        }

        return jwt.serialize();
    }

    // 參考 RFC-7518，僅支援最建議的演算法 ES256 和 RS256
    private JWSSigner getSignerByJwk(JWK jwk) throws JOSEException, UnsupportedAlgException {
        String alg = jwk.getAlgorithm().toString().toUpperCase();
        if ("ES256".equals(alg)) {
            return new ECDSASigner(jwk.toECKey());
        } else if ("RS256".equals(alg)) {
            return new RSASSASigner(jwk.toRSAKey());
        } else {
            String errMsg = String.format("Not support alg: %s", jwk.getAlgorithm().toString());
            throw new UnsupportedAlgException(errMsg);
        }
    }

    // 參考 RFC-7518，僅支援最建議的演算法 ES256 和 RS256
    private JWSVerifier getJWSVerifierByJwk(JWK jwk) throws JOSEException, UnsupportedAlgException {
        String alg = jwk.getAlgorithm().toString().toUpperCase();
        if ("ES256".equals(alg)) {
            return new ECDSAVerifier(jwk.toECKey().toECPublicKey());
        } else if ("RS256".equals(alg)) {
            return new RSASSAVerifier(jwk.toRSAKey().toRSAPublicKey());
        } else {
            String errMsg = String.format("Not support alg: %s", jwk.getAlgorithm().toString());
            throw new UnsupportedAlgException(errMsg);
        }
    }
}
