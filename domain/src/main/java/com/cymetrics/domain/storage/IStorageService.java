package com.cymetrics.domain.storage;

import com.cymetrics.domain.storage.enumeration.ResourceTypeEnum;
import com.cymetrics.domain.storage.exception.ObjectAlreadyExistException;
import com.cymetrics.domain.storage.exception.ObjectNotFoundException;
import com.cymetrics.domain.storage.resource.StorageResource;

import java.util.ArrayList;

public interface IStorageService {

    ArrayList<String> listFiles(ResourceTypeEnum type);

    boolean downloadFile(StorageResource resource) throws ObjectNotFoundException;

    boolean deleteFile(StorageResource resource) throws ObjectNotFoundException;

    boolean createFile(StorageResource resource) throws ObjectAlreadyExistException;

    boolean updateFile(StorageResource resource) throws ObjectNotFoundException;
}
