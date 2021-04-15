package hk.onedegree.domain.auth.services;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTClaimsSetVerifier;
import hk.onedegree.domain.auth.aggregates.user.User;
import hk.onedegree.domain.auth.constant.Constant;
import hk.onedegree.domain.auth.exceptions.RepositoryOperatorException;
import hk.onedegree.domain.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.text.ParseException;
import java.time.Clock;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

public class AuthenticationService {
    @Inject
    UserRepository userRepository;

    @Inject
    JWKSource<SecurityContext> jwkSource;

    Clock clock = Clock.systemUTC();

    private JWTClaimsSetVerifier jwtClaimsVerifier = new DefaultJWTClaimsVerifier(
            new JWTClaimsSet.Builder().issuer(Constant.issuer).build(),
            new HashSet<>(Arrays.asList("sub", "iat", "exp")));

    private static Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    public Optional<User> authenticate(String email, String password) throws RepositoryOperatorException {
        Optional<User> option = userRepository.findByEmail(email);
        if(option.isEmpty() || option.get().isPasswordMatch(password)) {
            return option;
        }

        return Optional.empty();
    }

    public Optional<User> authenticate(String token) {
        SignedJWT signedJWT = null;
        try {
            signedJWT = SignedJWT.parse(token);
        } catch (ParseException e) {
            logger.error("Parse jwt str error, exception: ", e);
            return Optional.empty();
        }

        ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();

        JWSKeySelector<SecurityContext> jwsKeySelector =
                new JWSVerificationKeySelector<>(signedJWT.getHeader().getAlgorithm(), jwkSource);

        jwtProcessor.setJWSKeySelector(jwsKeySelector);
        jwtProcessor.setJWTClaimsSetVerifier(this.jwtClaimsVerifier);

        try {
            JWTClaimsSet claimsSet = jwtProcessor.process(signedJWT, null);
            return this.userRepository.findById(claimsSet.getSubject());
        } catch (BadJOSEException | JOSEException | RepositoryOperatorException e) {
            logger.error("Get user from claim set in jwt fails, e: ", e);
            return Optional.empty();
        }
    }
}
