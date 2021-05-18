package com.cymetrics.domain.storage;

import com.cymetrics.domain.storage.enumeration.ResourceType;
import com.cymetrics.domain.storage.exception.ObjectAlreadyExistException;
import com.cymetrics.domain.storage.exception.ObjectNotFoundException;
import com.cymetrics.domain.storage.resource.IStorageResource;

import java.util.ArrayList;
import java.util.Optional;

public interface IStorageService {

    ArrayList<String> listFiles(ResourceType type);

    Optional<IStorageResource> downloadFile(ResourceType type, String fileName, String downloadPath) throws ObjectNotFoundException;

    void deleteFile(ResourceType type, String fileName) throws ObjectNotFoundException;

    boolean createFile(IStorageResource resource, String fileName) throws ObjectAlreadyExistException;

    boolean updateFile(IStorageResource resource, String fileName) throws ObjectNotFoundException;
}
