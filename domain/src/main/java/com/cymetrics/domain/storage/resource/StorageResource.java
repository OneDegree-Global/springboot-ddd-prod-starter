package com.cymetrics.domain.storage.resource;

import com.cymetrics.domain.storage.enumeration.ResourceTypeEnum;
import com.cymetrics.domain.storage.resource.checker.IResourceChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Optional;

public class StorageResource {

    protected static Logger logger = LoggerFactory.getLogger(StorageResource.class);
    protected File file;
    Optional<IResourceChecker> checker;
    private String localFileName;
    private String remoteFileName;
    private ResourceTypeEnum type;

    public StorageResource(String localFileName, String remoteFileName, ResourceTypeEnum type) {
        this.localFileName = localFileName;
        this.remoteFileName = remoteFileName;
        this.type = type;
        this.checker = ResourceCheckerFactory.getInstance().getResourceChecker(type);
    }

    public StorageResource(String remoteFileName, ResourceTypeEnum type) {
        this.localFileName = "";
        this.remoteFileName = remoteFileName;
        this.type = type;
        this.checker = ResourceCheckerFactory.getInstance().getResourceChecker(type);
    }


    final public ResourceTypeEnum getResourceType() {
        return type;
    }

    final public String getLocalFileName() {
        return localFileName;
    }

    final public String getRemoteFileName() {
        return remoteFileName;
    }

    public Optional<File> getResource() {
        return Optional.ofNullable(file);
    }

    public boolean setResource(File file) {
        if (checker.isPresent() && !checker.get().checkResource(file)) {
            return false;
        }
        this.file = file;
        return true;
    }

    public boolean loadResource() {
        File file = new File(localFileName);
        return setResource(file);
    }

}
