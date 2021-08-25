package com.cymetrics.messaging.implementation.utils;

import com.cymetrics.domain.messaging.types.JsonMessage;
import org.apache.commons.io.serialization.ValidatingObjectInputStream;

import java.io.*;

public class ObjectByteConverter {

    private ObjectByteConverter(){}
    public static byte[] encodeObject(Object obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(obj);
        return bos.toByteArray();
    }


    public static Object decodeObject(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ValidatingObjectInputStream vois = new ValidatingObjectInputStream(bis);
        vois.accept(
                java.lang.String.class,
                JsonMessage.class
                );
        // TODO: until message objects have been defined, we cannot create white list
        return vois.readObject();
    }
}
