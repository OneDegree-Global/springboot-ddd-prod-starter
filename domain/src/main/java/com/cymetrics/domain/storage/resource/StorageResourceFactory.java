package com.cymetrics.domain.storage.resource;

import com.cymetrics.domain.storage.enumeration.ResourceType;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

public class StorageResourceFactory {

    static StorageResourceFactory instance;

    public synchronized static StorageResourceFactory createInstance() throws IOException, TimeoutException {
        if( instance == null){
            instance = new StorageResourceFactory();
        }
        return instance;
    }

    public static StorageResourceFactory getInstance() throws IOException, TimeoutException{
        if( instance == null){
            instance = createInstance();
        }
        return instance;
    }

    public IStorageResource getEmptyResource(ResourceType type){
       switch (type){
           case smallimage:
               return new SmallImageResource();
       }
       return null;
    }
}
