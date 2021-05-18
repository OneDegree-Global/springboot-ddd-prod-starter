package com.cymetrics.storage.implementation;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import com.cymetrics.domain.storage.IStorageService;
import com.cymetrics.domain.storage.enumeration.ResourceType;
import com.cymetrics.domain.storage.exception.ObjectAlreadyExistException;
import com.cymetrics.domain.storage.exception.ObjectNotFoundException;
import com.cymetrics.domain.storage.resource.IStorageResource;
import com.cymetrics.domain.storage.resource.StorageResourceFactory;
import com.cymetrics.storage.implementation.configs.AzureBlobConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

public class StorageServiceAzureBlobImp implements IStorageService {

    private static Logger logger = LoggerFactory.getLogger(StorageServiceAzureBlobImp.class);
    AzureBlobConfig config;
    BlobServiceClient serviceClient;

    @Inject
    public StorageServiceAzureBlobImp(AzureBlobConfig config) {
        this.config = config;
        ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
                .clientId(this.config.getCLIENT_ID())
                .clientSecret(this.config.getCLIENT_SECRET())
                .tenantId(this.config.getTENANT_ID())
                .build();


        serviceClient = new BlobServiceClientBuilder().connectionString(config.getCONNECTION_STRING()).buildClient();
    }

    @Override
    public ArrayList<String> listFiles(ResourceType type) {
        BlobContainerClient containerClient = serviceClient.getBlobContainerClient(type.name());
        ArrayList<String> resourceList = new ArrayList<>();
        for (BlobItem blobItem : containerClient.listBlobs()) {
            resourceList.add(blobItem.getName());
        }
        return resourceList;
    }


    @Override
    public Optional<IStorageResource> downloadFile(ResourceType type, String fileName, String downloadPath)
            throws ObjectNotFoundException {
        IStorageResource resource = null;
        File downloadedFile;
        try {
            BlobClient client = connectAndCheckExist(type, fileName);

            client.downloadToFile(downloadPath);
            resource = StorageResourceFactory.getInstance().getEmptyResource(type);
            downloadedFile = new File(downloadPath);
        } catch (FileAlreadyExistsException e) {
            downloadedFile = new File(downloadPath);
        } catch (IOException | TimeoutException e) {
            logger.error("download file from azure blob fail:" + e.toString());
            return Optional.empty();
        }
        if (!downloadedFile.exists() || !resource.setResource(downloadedFile)) {
            return Optional.empty();
        }
        return Optional.of(resource);
    }


    @Override
    public void deleteFile(ResourceType type, String fileName) throws ObjectNotFoundException {
        BlobClient client = connectAndCheckExist(type, fileName);
        client.delete();
    }

    @Override
    public boolean createFile(IStorageResource resource, String fileName) throws ObjectAlreadyExistException {
        if (resource.getResource().isEmpty())
            return false;
        BlobClient client = connectAndCheckNotExist(resource.getResourceType(), fileName);
        try {
            client.upload(Files.newInputStream(resource.getResource().get().toPath()), resource.getResource().get().length());
        } catch (IOException e) {
            logger.error("upload file `" + fileName + "`to azure blob failed:" + e.toString());
        }
        return true;
    }

    @Override
    public boolean updateFile(IStorageResource resource, String fileName) throws ObjectNotFoundException {
        if (resource.getResource().isEmpty())
            return false;
        BlobClient client = connectAndCheckExist(resource.getResourceType(), fileName);
        try {
            client.upload(Files.newInputStream(resource.getResource().get().toPath()), resource.getResource().get().length(), true);
        } catch (IOException e) {
            logger.error("update file `" + fileName + "`to azure blob failed:" + e.toString());
        }
        return true;
    }

    private BlobClient connectAndCheckExist(ResourceType type, String fileName) throws ObjectNotFoundException {
        BlobContainerClient containerClient = serviceClient.getBlobContainerClient(type.name());
        BlobClient client = containerClient.getBlobClient(fileName);
        if (!client.exists())
            throw new ObjectNotFoundException("file: " + fileName + " type: " + type.name() + " does not exist");
        return client;
    }

    private BlobClient connectAndCheckNotExist(ResourceType type, String fileName) throws ObjectAlreadyExistException {
        BlobContainerClient containerClient = serviceClient.getBlobContainerClient(type.name());
        BlobClient client = containerClient.getBlobClient(fileName);
        if (client.exists())
            throw new ObjectAlreadyExistException("file: " + fileName + " type: " + type.name() + " aleady exist!");
        return client;
    }

}
