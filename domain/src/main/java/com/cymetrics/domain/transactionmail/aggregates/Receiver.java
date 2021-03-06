package com.cymetrics.domain.transactionmail.aggregates;
import lombok.Getter;
import com.cymetrics.domain.transactionmail.exceptions.InvalidEmailFormat;
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
