package hk.onedegree.domain.auth.service;

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
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

class AuthTokenService {

    @Inject
    private JWKSet jwkSet;

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
            logger.debug("Parse jwt str error, exception: ", e);
            return false;
        }

        JWK jwk = this.jwkSet.getKeyByKeyId(signedJWT.getHeader().getKeyID());
        JWSVerifier verifier = null;
        try {
            verifier = getJWSVerifierByJwk(jwk);
        } catch (JOSEException e) {
            logger.debug("Get jwt verifier error, exception: ", e);
            return false;
        } catch (UnsupportedAlgException e) {
            logger.debug("Unsupported alg in jwk: {}", jwk.getAlgorithm().toString());
            return false;
        }

        try {
            return signedJWT.verify(verifier);
        } catch (JOSEException e) {
            logger.debug("Verifier jwt error, exception: ", e);
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

        LocalDateTime dateTime = LocalDateTime.now().plus(Duration.of(10, ChronoUnit.MINUTES));
        Date expireDate = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(issuer)
                .issueTime(new Date())
                .expirationTime(expireDate)
                .subject(user.getEmail())
                .build();

        SignedJWT jwt = new SignedJWT(header, claimsSet);

        try {
            jwt.sign(getSignerByJwk(latestJwk));
        } catch (JOSEException e) {
            logger.debug("Sign jwt error, exception: ", e);
            return "";
        } catch (UnsupportedAlgException e) {
            logger.debug("Unsupported alg in jwk: {}", latestJwk.getAlgorithm().toString());
            return "";
        }

        return jwt.serialize();
    }

    private JWSSigner getSignerByJwk(JWK jwk) throws JOSEException, UnsupportedAlgException {
        if ("ES256".equals(jwk.getAlgorithm().toString())) {
            return new ECDSASigner(jwk.toECKey());
        } else if ("RS256".equals(jwk.getAlgorithm().toString())) {
            return new RSASSASigner(jwk.toRSAKey());
        } else {
            String errMsg = String.format("Not support alg: %s", jwk.getAlgorithm().toString());
            throw new UnsupportedAlgException(errMsg);
        }
    }

    private JWSVerifier getJWSVerifierByJwk(JWK jwk) throws JOSEException, UnsupportedAlgException {
        if ("ES256".equals(jwk.getAlgorithm().toString())) {
            return new ECDSAVerifier(jwk.toECKey().toECPublicKey());
        } else if ("RS256".equals(jwk.getAlgorithm().toString())) {
            return new RSASSAVerifier(jwk.toRSAKey().toRSAPublicKey());
        } else {
            String errMsg = String.format("Not support alg: %s", jwk.getAlgorithm().toString());
            throw new UnsupportedAlgException(errMsg);
        }
    }
}
