package com.cymetrics.transaction_mail.model;

import com.cymetrics.transaction_mail.exceptions.InvalidEmailFormat;
import com.cymetrics.transaction_mail.model.Receiver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class ReceiverTest {

    @Test
    @DisplayName("If invalid email is provided, corresponding exception should be raised")
    public void construct_invalidEmail_InvalidEmailException() {
        Assertions.assertThrows(InvalidEmailFormat.class, () -> {
           String id = UUID.randomUUID().toString();
           String userName = "Mike Trout";
           String invalidEmail = "mike.trout@@mlb.com";
            new Receiver(id, invalidEmail, userName);
        });
    }

    @Test
    @DisplayName("Receiver instance should be created successfully out of correct data")
    public void construct_valid_email() throws InvalidEmailFormat {
        String id = UUID.randomUUID().toString();
        String userName = "Mike Trout";
        String invalidEmail = "mike.trout@mlb.com";
        new Receiver(id, invalidEmail, userName);
    }

}
