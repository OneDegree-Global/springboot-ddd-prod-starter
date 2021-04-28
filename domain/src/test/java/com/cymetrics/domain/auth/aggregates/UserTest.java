package com.cymetrics.domain.auth.aggregates;

import com.cymetrics.domain.auth.aggregates.user.User;
import com.cymetrics.domain.auth.exceptions.InValidEmailException;
import com.cymetrics.domain.auth.exceptions.InValidPasswordException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

public class UserTest {
    @Test
    public void construct_InvalidEmail_ThrowInValidEmailException() {
        Assertions.assertThrows(InValidEmailException.class, () -> {
            String id = UUID.randomUUID().toString();
            String email = "123@gmail.com.123";

            new User(id, email);
        });
    }

    @Test
    public void construct_ValidEmail() throws InValidEmailException {
        String id = UUID.randomUUID().toString();
        String email = "123@gmail.com";

        new User(id, email);
    }

    @Test
    public void construct_UppercaseEmail_ConvertToLowercase() throws InValidEmailException {
        String id = UUID.randomUUID().toString();
        String email = "123@GMAIL.COM";

        User user = new User(id, email);
        Assertions.assertEquals(email.toLowerCase(), user.getEmail());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "a", //too short: 8-30
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", //too long: 8-30
            "aaaaaaaaaa", //at least on uppercase English character
            "AAAAAAAAAA", //at least on lowercase English character
            "kkkk  kkkk", //no whitespace
    })
    public void setPassword_inValidPassword_ThrowInValidPasswordException(String password) {

        Assertions.assertThrows(InValidPasswordException.class, () -> {
            String id = UUID.randomUUID().toString();
            String email = "123@gmail.com";

            User user = new User(id, email);
            user.setPassword(password);
        });
    }

    @Test
    public void setPassword_ValidPassword() throws InValidEmailException, InValidPasswordException {
        String id = UUID.randomUUID().toString();
        String email = "123@gmail.com";
        String password1 = "Aabcd1234567";
        String password2 = "Aabcd1234567";

        User user = new User(id, email);
        user.setPassword(password1);
        user.isPasswordMatch(password2);
    }

    @Test
    public void setPassword_InValidPassword_ReturnTrue() throws InValidEmailException, InValidPasswordException {
        String id = UUID.randomUUID().toString();
        String email = "123@gmail.com";
        String password1 = "Aabcd1234567";
        String password2 = "Aabcd1234567";

        User user = new User(id, email);
        user.setPassword(password1);

        Assertions.assertTrue(user.isPasswordMatch(password2));
    }

    @Test
    public void setPassword_InValidPassword_ReturnFalse() throws InValidEmailException, InValidPasswordException {
        String id = UUID.randomUUID().toString();
        String email = "123@gmail.com";
        String password1 = "Aabcd1234567";
        String password2 = "Aabcd1234568";

        User user = new User(id, email);
        user.setPassword(password1);

        Assertions.assertFalse(user.isPasswordMatch(password2));
    }
}
