package com.cymetrics.storage.implementation;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import com.cymetrics.domain.storage.IStorageService;
import com.cymetrics.domain.storage.enumeration.ResourceTypeEnum;
import com.cymetrics.domain.storage.exception.ObjectAlreadyExistException;
import com.cymetrics.domain.storage.exception.ObjectNotFoundException;
import com.cymetrics.domain.storage.resource.StorageResource;
import com.cymetrics.storage.implementation.configs.AzureBlobConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

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
    public ArrayList<String> listFiles(ResourceTypeEnum type) {
        BlobContainerClient containerClient = serviceClient.getBlobContainerClient(type.name().toLowerCase());
        ArrayList<String> resourceList = new ArrayList<>();
        for (BlobItem blobItem : containerClient.listBlobs()) {
            resourceList.add(blobItem.getName());
        }
        return resourceList;
    }

    @Override
    public boolean downloadFile(StorageResource resource)
            throws ObjectNotFoundException {
        File downloadedFile;
        try {
            BlobClient client = connectAndCheckExist(resource.getResourceType(), resource.getRemoteFileName());

            client.downloadToFile(resource.getLocalFileName());
            downloadedFile = new File(resource.getLocalFileName());
        } catch (Exception e) {
            logger.error("download file from azure blob fail:" + e.toString());
            return false;
        }
        if (!downloadedFile.exists() || !resource.setResource(downloadedFile)) {
            return false;
        }
        return true;
    }


    @Override
    public boolean deleteFile(StorageResource resource) throws ObjectNotFoundException {
        BlobClient client = connectAndCheckExist(resource.getResourceType(), resource.getRemoteFileName());
        try {
            client.delete();
        } catch (Exception e) {
            logger.error("delete file " + resource.getRemoteFileName() + " from azure blob failed " + e.toString());
            return false;
        }
        return true;
    }

    @Override
    public boolean createFile(StorageResource resource) throws ObjectAlreadyExistException {
        if (resource.getResource().isEmpty() && !resource.loadResource()) {
            logger.error("local resource" + resource.getLocalFileName() + "does not exists");
            return false;
        }
        BlobClient client = connectAndCheckNotExist(resource.getResourceType(), resource.getRemoteFileName());
        try {
            client.upload(Files.newInputStream(resource.getResource().get().toPath()), resource.getResource().get().length());
        } catch (Exception e) {
            logger.error("upload file `" + resource.getLocalFileName() + "`to azure blob failed:" + e.toString());
            return false;
        }
        return true;
    }

    @Override
    public boolean updateFile(StorageResource resource) throws ObjectNotFoundException {
        if (resource.getResource().isEmpty() && !resource.loadResource()) {
            logger.error("local resource" + resource.getLocalFileName() + "does not exists");
            return false;
        }
        BlobClient client = connectAndCheckExist(resource.getResourceType(), resource.getRemoteFileName());
        try {
            client.upload(Files.newInputStream(resource.getResource().get().toPath()), resource.getResource().get().length(), true);
        } catch (Exception e) {
            logger.error("update file `" + resource.getLocalFileName() + "`to azure blob failed:" + e.toString());
            return false;
        }
        return true;
    }

    private BlobClient connectAndCheckExist(ResourceTypeEnum type, String fileName) throws ObjectNotFoundException {
        BlobContainerClient containerClient = serviceClient.getBlobContainerClient(type.name().toLowerCase());
        BlobClient client = containerClient.getBlobClient(fileName);
        if (!client.exists())
            throw new ObjectNotFoundException("file: " + fileName + " type: " + type.name() + " does not exist");
        return client;
    }

    private BlobClient connectAndCheckNotExist(ResourceTypeEnum type, String fileName) throws ObjectAlreadyExistException {
        BlobContainerClient containerClient = serviceClient.getBlobContainerClient(type.name().toLowerCase());
        BlobClient client = containerClient.getBlobClient(fileName);
        if (client.exists())
            throw new ObjectAlreadyExistException("file: " + fileName + " type: " + type.name() + " aleady exist!");
        return client;
    }

}
