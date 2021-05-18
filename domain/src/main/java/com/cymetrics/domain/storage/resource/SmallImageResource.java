package com.cymetrics.domain.storage.resource;

import com.cymetrics.domain.storage.enumeration.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class SmallImageResource implements IStorageResource {

    private static Logger logger = LoggerFactory.getLogger(SmallImageResource.class);
    File image;

    @Override
    public boolean setResource(File image) {

        if (image.length() > 104857600) {
            return false;
        }

        try {
            if (ImageIO.read(image) == null) {
                logger.error("small image resource cannot be read correctly!");
                return false;
            }
        } catch (IOException ex) {
            logger.error("small image resource cannot be read correctly!");
            return false;
        }

        this.image = image;
        return true;
    }

    @Override
    public Optional<File> getResource() {
        return Optional.ofNullable(image);
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.smallimage;
    }

}
