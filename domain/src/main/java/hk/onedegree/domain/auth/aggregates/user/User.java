package hk.onedegree.domain.auth.aggregates.user;

import hk.onedegree.domain.auth.exceptions.InValidEmailException;
import hk.onedegree.domain.auth.exceptions.InValidPasswordException;
import lombok.Getter;
import org.apache.commons.validator.routines.EmailValidator;
import org.passay.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;

public class User {

    @Getter
    private final String id;

    @Getter
    private final String email;

    public User(String id, String email) throws InValidEmailException {
        this.id = id;
        
        if(!this.emailValidator.isValid(email)) {
            String errMsg = String.format("Invalid email: %s", email);
            throw new InValidEmailException(errMsg);
        }

        this.email = email.toLowerCase();
    }

    private String hashedPassword;

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
