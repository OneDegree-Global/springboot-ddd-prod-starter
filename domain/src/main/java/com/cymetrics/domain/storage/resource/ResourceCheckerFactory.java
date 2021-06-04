package com.cymetrics.domain.storage.resource;

import com.cymetrics.domain.storage.enumeration.ResourceTypeEnum;
import com.cymetrics.domain.storage.resource.checker.IResourceChecker;
import com.cymetrics.domain.storage.resource.checker.SmallImageChecker;

import java.util.Optional;

public class ResourceCheckerFactory {

    private static ResourceCheckerFactory instance;

    public synchronized static ResourceCheckerFactory createInstance() {
        if (instance == null) {
            instance = new ResourceCheckerFactory();
        }
        return instance;
    }

    public static ResourceCheckerFactory getInstance() {
        if (instance == null) {
            instance = createInstance();
        }
        return instance;
    }

    public Optional<IResourceChecker> getResourceChecker(ResourceTypeEnum type) {
        switch (type) {
            case smallImage:
                return Optional.of(new SmallImageChecker());
            default:
                return Optional.empty();
        }
    }
}
