package com.cymetrics.storage.implementation;

import com.cymetrics.domain.storage.enumeration.ResourceTypeEnum;
import com.cymetrics.domain.storage.exception.ObjectAlreadyExistException;
import com.cymetrics.domain.storage.exception.ObjectNotFoundException;
import com.cymetrics.domain.storage.resource.StorageResource;
import com.cymetrics.storage.implementation.configs.AzureBlobConfig;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

@Tag("slow")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AzureBlobTest {

    static StorageServiceAzureBlobImp azureBlob;
    private static Logger logger = LoggerFactory.getLogger(StorageServiceAzureBlobImp.class);

    @BeforeAll
    public static void init() {
        Assumptions.assumeTrue(System.getenv("AZURE_CONNECTION_STRING") != null);

        // TODO: find a better way to get key list
        AzureBlobConfig config = new AzureBlobConfig(
                System.getenv("AZURE_TENANT_ID"),
                System.getenv("AZURE_CLIENT_ID"),
                System.getenv("AZURE_CLIENT_SECRET"),
                System.getenv("AZURE_KEYVAULT_URL"),
                System.getenv("AZURE_STORAGEACCOUNT_CONNECTIONSTRING_NAME"),
                System.getenv("AZURE_STORAGEACCOUNT_CONNECTIONSTRING_VERSION"),
                System.getenv("AZURE_CONNECTION_STRING")
        );
        azureBlob = new StorageServiceAzureBlobImp(config);

    }

    @AfterAll
    public static void clean() {
        Assumptions.assumeTrue(System.getenv("AZURE_CONNECTION_STRING") != null);
        try {
            StorageResource resource = new StorageResource("test_cymetrics2.png", ResourceTypeEnum.smallImage);
            azureBlob.deleteFile(resource);
        } catch (ObjectNotFoundException e) {
            logger.info("file to be delete not exist");
        }
        File[] testFiles = new File("./src/test/java/com/cymetrics/storage/implementation/resource/").listFiles((FilenameFilter) new WildcardFileFilter("test*.png"));
        for (File f : testFiles) {
            if (f.exists())
                f.delete();
        }
    }

    @Test
    @Order(1)
    public void create_list_Blob() throws ObjectAlreadyExistException {

        Assumptions.assumeTrue(System.getenv("AZURE_CONNECTION_STRING") != null);

        StorageResource resource1 = new StorageResource("./src/test/java/com/cymetrics/storage/implementation/resource/cymetrics.png",
                "test_cymetrics1.png", ResourceTypeEnum.smallImage);
        StorageResource resource2 = new StorageResource("./src/test/java/com/cymetrics/storage/implementation/resource/cymetrics.png",
                "test_cymetrics2.png", ResourceTypeEnum.smallImage);


        azureBlob.createFile(resource1);
        azureBlob.createFile(resource2);

        ArrayList<String> fileList = azureBlob.listFiles(resource1.getResourceType());
        Assertions.assertEquals(2, fileList.size());
        Assertions.assertEquals("test_cymetrics1.png", fileList.get(0));
        Assertions.assertEquals("test_cymetrics2.png", fileList.get(1));

    }

    @Test
    @Order(2)
    public void getBlob() throws ObjectNotFoundException, IOException {
        Assumptions.assumeTrue(System.getenv("AZURE_CONNECTION_STRING") != null);

        StorageResource resource = new StorageResource("./src/test/java/com/cymetrics/storage/implementation/resource/test_get_blob.png",
                "test_cymetrics1.png", ResourceTypeEnum.smallImage);
        azureBlob.downloadFile(resource);
        File downloadedFile = new File(resource.getLocalFileName());
        File originFile = new File("./src/test/java/com/cymetrics/storage/implementation/resource/cymetrics.png");
        if (!downloadedFile.exists())
            Assertions.fail("file not download correctly!");

        Assertions.assertTrue(FileUtils.contentEquals(downloadedFile, originFile));
    }

    @Test
    @Order(3)
    public void updateBlob() throws ObjectNotFoundException, IOException {
        Assumptions.assumeTrue(System.getenv("AZURE_CONNECTION_STRING") != null);

        StorageResource resource1 = new StorageResource("./src/test/java/com/cymetrics/storage/implementation/resource/onedegree.png"
                , "test_cymetrics1.png", ResourceTypeEnum.smallImage);
        StorageResource resource2 = new StorageResource("./src/test/java/com/cymetrics/storage/implementation/resource/test_update_blob.png"
                , "test_cymetrics1.png", ResourceTypeEnum.smallImage);

        azureBlob.updateFile(resource1);
        azureBlob.downloadFile(resource2);

        File fileToUpdate = new File(resource1.getLocalFileName());
        File updatedFile = new File(resource2.getLocalFileName());

        Assertions.assertTrue(FileUtils.contentEquals(fileToUpdate, updatedFile));
    }

    @Test
    @Order(4)
    public void deleteBlob() throws ObjectNotFoundException {
        Assumptions.assumeTrue(System.getenv("AZURE_CONNECTION_STRING") != null);

        StorageResource resource = new StorageResource("test_cymetrics1.png", ResourceTypeEnum.smallImage);
        azureBlob.deleteFile(resource);
        ArrayList<String> fileList = azureBlob.listFiles(ResourceTypeEnum.smallImage);
        Assertions.assertEquals(1, fileList.size());
    }


}
