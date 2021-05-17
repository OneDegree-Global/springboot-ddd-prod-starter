package com.cymetrics.web.springboot.config.di;

import com.cymetrics.storage.implementation.configs.AzureBlobConfig;
import org.apache.commons.lang.NullArgumentException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Storage {
    @Bean
    public AzureBlobConfig azureBlobConfigBean(){
        if( System.getenv("AZURE_TENANT_ID")== null ||
                System.getenv("AZURE_CLIENT_ID") == null ||
                System.getenv("AZURE_CLIENT_SECRET") == null ||
                System.getenv("AZURE_KEYVAULT_URL") == null ||
                System.getenv("AZURE_STORAGEACCOUNT_CONNECTIONSTRING_NAME") == null ||
                System.getenv("AZURE_STORAGEACCOUNT_CONNECTIONSTRING_VERSION") == null||
                System.getenv("AZURE_CONNECTION_STRING") == null
        )
            throw new NullArgumentException("environment varialbe required to start azure blob not provided");

        AzureBlobConfig config = new AzureBlobConfig(
                System.getenv("AZURE_TENANT_ID"),
                System.getenv("AZURE_CLIENT_ID"),
                System.getenv("AZURE_CLIENT_SECRET"),
                System.getenv("AZURE_KEYVAULT_URL"),
                System.getenv("AZURE_STORAGEACCOUNT_CONNECTIONSTRING_NAME"),
                System.getenv("AZURE_STORAGEACCOUNT_CONNECTIONSTRING_VERSION"),
                System.getenv("AZURE_CONNECTION_STRING")
        );
        return config;
    }
}