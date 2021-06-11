package com.cymetrics.domain.storage.resource.checker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class SmallImageChecker implements IResourceChecker {

    protected static Logger logger = LoggerFactory.getLogger(SmallImageChecker.class);

    @Override
    public boolean checkResource(File file) {
        if (file.length() > 104857600) {
            return false;
        }

        try {
            if (ImageIO.read(file) == null) {
                logger.error("small image resource cannot be read correctly!");
                return false;
            }
        } catch (IOException ex) {
            logger.error("small image resource cannot be read correctly!");
            return false;
        }

        return true;
    }

}
