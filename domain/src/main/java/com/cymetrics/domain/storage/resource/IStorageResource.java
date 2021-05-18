package com.cymetrics.domain.storage.resource;

import com.cymetrics.domain.storage.enumeration.ResourceType;

import java.io.File;
import java.util.Optional;

public interface IStorageResource {
    boolean setResource(File file);

    Optional<File> getResource();

    ResourceType getResourceType();
}
