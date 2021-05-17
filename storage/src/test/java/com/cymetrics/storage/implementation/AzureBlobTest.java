package com.cymetrics.storage.implementation;

import com.cymetrics.domain.storage.enumeration.ResourceType;
import com.cymetrics.domain.storage.exception.ObjectAlreadyExistException;
import com.cymetrics.domain.storage.exception.ObjectNotFoundException;
import com.cymetrics.domain.storage.resource.SmallImageResource;
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
    public static void init(){
        Assumptions.assumeTrue(System.getenv("AZURE_CONNECTION_STRING") != null);

        AzureBlobConfig config = new AzureBlobConfig(
            System.getenv("AZURE_TENANT_ID"),
            System.getenv("AZURE_CLIENT_ID"),
            System.getenv("AZURE_CLIENT_SECRET"),
            System.getenv("AZURE_KEYVAULT_URL"),
            System.getenv("AZURE_STORAGEACCOUNT_CONNECTIONSTRING_NAME"),
            System.getenv("AZURE_STORAGEACCOUNT_CONNECTIONSTRING_VERSION"), System.getenv("AZURE_CONNECTION_STRING")
        );
        azureBlob = new StorageServiceAzureBlobImp(config);

    }

    @AfterAll
    public static void clean() {
        Assumptions.assumeTrue(System.getenv("AZURE_CONNECTION_STRING") != null);
        try {
            azureBlob.deleteFile(ResourceType.smallimage, "test_cymetrics2.png");
        } catch(ObjectNotFoundException e){
            logger.info("file to be delete not exist");
        }
        File[] testFiles = new File("./src/test/java/com/cymetrics/storage/implementation/resource/").listFiles((FilenameFilter) new WildcardFileFilter("test*.png"));
        for(File f : testFiles){
            if(f.exists())
                f.delete();
        }
    }

    @Test
    @Order(1)
    public void create_list_Blob() throws ObjectAlreadyExistException {

        Assumptions.assumeTrue(System.getenv("AZURE_CONNECTION_STRING") != null);

        SmallImageResource resource = new SmallImageResource();
        File image = new File("./src/test/java/com/cymetrics/storage/implementation/resource/cymetrics.png");
        resource.setResource(image);

        azureBlob.createFile(resource,"test_cymetrics1.png");
        azureBlob.createFile(resource,"test_cymetrics2.png");

        ArrayList<String> fileList = azureBlob.listFiles(resource.getResourceType());
        Assertions.assertEquals(2, fileList.size());
        Assertions.assertEquals("test_cymetrics1.png", fileList.get(0));
        Assertions.assertEquals("test_cymetrics2.png", fileList.get(1));

    }

    @Test
    @Order(2)
    public void getBlob() throws ObjectNotFoundException, IOException {
        Assumptions.assumeTrue(System.getenv("AZURE_CONNECTION_STRING") != null);

        azureBlob.downloadFile(ResourceType.smallimage,"test_cymetrics1.png","./src/test/java/com/cymetrics/storage/implementation/resource/test_get_blob.png");
        File downloadedFile = new File("./src/test/java/com/cymetrics/storage/implementation/resource/test_get_blob.png");
        File originFile = new File("./src/test/java/com/cymetrics/storage/implementation/resource/cymetrics.png");
        if(!downloadedFile.exists())
            Assertions.fail("file not download correctly!");

        Assertions.assertTrue( FileUtils.contentEquals(downloadedFile, originFile));
    }

    @Test
    @Order(3)
    public void updateBlob() throws ObjectNotFoundException, IOException {
        Assumptions.assumeTrue(System.getenv("AZURE_CONNECTION_STRING") != null);

        SmallImageResource resource = new SmallImageResource();
        File fileToUpdate = new File("./src/test/java/com/cymetrics/storage/implementation/resource/onedegree.png");
        resource.setResource(fileToUpdate);
        azureBlob.updateFile(resource, "test_cymetrics1.png");
        azureBlob.downloadFile(ResourceType.smallimage,"test_cymetrics1.png","./src/test/java/com/cymetrics/storage/implementation/resource/test_update_blob.png");

        File updatedFile = new File("./src/test/java/com/cymetrics/storage/implementation/resource/test_update_blob.png");

        Assertions.assertTrue( FileUtils.contentEquals(fileToUpdate , updatedFile) );
    }

    @Test
    @Order(4)
    public void deleteBlob() throws ObjectNotFoundException {
        Assumptions.assumeTrue(System.getenv("AZURE_CONNECTION_STRING") != null);

        azureBlob.deleteFile(ResourceType.smallimage, "test_cymetrics1.png");
        ArrayList<String> fileList = azureBlob.listFiles(ResourceType.smallimage);
        Assertions.assertEquals(1,fileList.size());
    }



}
