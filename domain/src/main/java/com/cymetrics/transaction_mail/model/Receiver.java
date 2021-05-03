package com.cymetrics.transaction_mail.model;
import lombok.Getter;
import lombok.NonNull;
import com.cymetrics.transaction_mail.exceptions.InvalidEmailFormat;

@Getter
public class Receiver {

    private String id;
    private String address;
    private String userName;

    public Receiver(String id, String address, String userName) throws InvalidEmailFormat {
        this.id = id;
        this.address = address;
        this.userName = userName;
    }

}
