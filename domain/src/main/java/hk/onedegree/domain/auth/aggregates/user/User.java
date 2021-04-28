package hk.onedegree.domain.auth.aggregates.user;

import hk.onedegree.domain.auth.exceptions.InValidEmailException;
import hk.onedegree.domain.auth.exceptions.InValidPasswordException;
import lombok.Getter;
import org.apache.commons.validator.routines.EmailValidator;
import org.passay.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.regex.Pattern;

public class User {

    @Getter
    private final String id;

    @Getter
    private final String email;

    @Getter
    private String hashedPassword;

    private Pattern BCRYPT_PATTERN = Pattern.compile("\\A\\$2(a|y|b)?\\$(\\d\\d)\\$[./0-9A-Za-z]{53}");

    public User(String id, String email) throws InValidEmailException {
        this.id = id;
        
        if(!this.emailValidator.isValid(email)) {
            String errMsg = String.format("Invalid email: %s", email);
            throw new InValidEmailException(errMsg);
        }

        this.email = email.toLowerCase();
    }

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private EmailValidator emailValidator = EmailValidator.getInstance();

    public void setPassword(String password) throws InValidPasswordException {
        PasswordValidator validator = new PasswordValidator(Arrays.asList(
                new LengthRule(8, 30),
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new WhitespaceRule()));
        RuleResult result = validator.validate(new PasswordData(password));

        if(!result.isValid()){
            String errMsg = String.format("Invalid password, reason: %s", result.getDetails().toString());
            throw new InValidPasswordException(errMsg);
        }

        this.hashedPassword = this.passwordEncoder.encode(password);
    }

    public void setHashedPassword(String hashedPassword) throws InValidPasswordException {
        if (!this.BCRYPT_PATTERN.matcher(hashedPassword).matches()) {
            throw new InValidPasswordException("HashedPassword does not look like BCrypt");
        }
        this.hashedPassword = hashedPassword;
    }

    public boolean isPasswordMatch(String password) {
        return this.passwordEncoder.matches(password, this.hashedPassword);
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

}
