package com.cymetrics.transaction_mail.model;
import lombok.Getter;
import lombok.NonNull;
import com.cymetrics.transaction_mail.exceptions.InvalidEmailFormat;
import org.apache.commons.validator.routines.EmailValidator;

@Getter
public class Receiver {

    private String id;
    private String email;
    private String userName;

    private EmailValidator emailValidator = EmailValidator.getInstance();

    public Receiver(String id, String email, String userName) throws InvalidEmailFormat {
        this.id = id;
        this.userName = userName;

        if (!this.emailValidator.isValid(email)) {
            String errMsg = String.format("Invalid email format: %s", email);
            throw new InvalidEmailFormat(errMsg);
        }

        this.email = email;
    }

}
