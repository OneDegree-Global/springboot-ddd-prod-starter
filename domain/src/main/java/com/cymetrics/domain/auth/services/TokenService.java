package com.cymetrics.domain.auth.services;

import com.cymetrics.domain.auth.aggregates.user.User;
import com.cymetrics.domain.auth.constant.Constant;
import com.cymetrics.domain.auth.exceptions.UnsupportedAlgException;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

public class TokenService {

    @Inject
    JWKSet jwkSet;

    Clock clock = Clock.systemUTC();

    private static Logger logger = LoggerFactory.getLogger(TokenService.class);

    public Optional<String> issueToken(Optional<User> optional) {
        if (optional.isEmpty()) {
            return Optional.of("");
        }

        User user = optional.get();

        JWK latestJwk = this.jwkSet.getKeys().get(0);
        String alg = latestJwk.getAlgorithm().toString();

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.parse(alg))
                .type(JOSEObjectType.JWT)
                .keyID(latestJwk.getKeyID())
                .build();


        Instant curInstant = clock.instant();
        long iat = curInstant.toEpochMilli();
        long exp = curInstant.plus(1, ChronoUnit.DAYS).toEpochMilli();

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .issuer(Constant.issuer)
                .issueTime(new Date(iat))
                .expirationTime(new Date(exp))
                .subject(user.getId())
                .build();

        SignedJWT jwt = new SignedJWT(header, claimsSet);

        try {
            jwt.sign(getSignerByJwk(latestJwk));
        } catch (JOSEException e) {
            logger.error("Sign jwt error, exception: ", e);
            return Optional.of("");
        } catch (UnsupportedAlgException e) {
            logger.error("Unsupported alg in jwk: {}", latestJwk.getAlgorithm().toString());
            return Optional.of("");
        }

        return Optional.of(jwt.serialize());
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
}
