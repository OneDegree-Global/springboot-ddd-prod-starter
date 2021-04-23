package com.odhk.messaging.implementation.utils;

import java.io.*;

public class ObjectByteConverter {
    synchronized static public byte[] encodeObject(Object obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(obj);
        return bos.toByteArray();
    }


    synchronized static public Object decodeObject(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        // TODO: until message objects have been defined, we cannot create white list
        ObjectInputStream in = new ObjectInputStream(bis);
        return in.readObject();
    }
}
