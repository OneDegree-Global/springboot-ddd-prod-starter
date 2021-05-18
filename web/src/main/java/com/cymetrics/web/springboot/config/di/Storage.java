package com.cymetrics.web.springboot.config.di;

import com.cymetrics.storage.implementation.configs.AzureBlobConfig;
import org.apache.commons.lang.NullArgumentException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Configuration
public class Storage {
    @Bean
    public AzureBlobConfig azureBlobConfigBean() throws IOException {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream("azureblob.properties");

        Properties properties = new Properties();
        properties.load(input);


        if( properties.getProperty("AZURE_TENANT_ID")== null ||
                properties.getProperty("AZURE_CLIENT_ID") == null ||
                properties.getProperty("AZURE_CLIENT_SECRET") == null ||
                properties.getProperty("AZURE_KEYVAULT_URL") == null ||
                properties.getProperty("AZURE_STORAGEACCOUNT_CONNECTIONSTRING_NAME") == null ||
                properties.getProperty("AZURE_STORAGEACCOUNT_CONNECTIONSTRING_VERSION") == null||
                properties.getProperty("AZURE_CONNECTION_STRING") == null
        )
            throw new NullArgumentException("environment varialbe required to start azure blob not provided");

        AzureBlobConfig config = new AzureBlobConfig(
                properties.getProperty("AZURE_TENANT_ID"),
                properties.getProperty("AZURE_CLIENT_ID"),
                properties.getProperty("AZURE_CLIENT_SECRET"),
                properties.getProperty("AZURE_KEYVAULT_URL"),
                properties.getProperty("AZURE_STORAGEACCOUNT_CONNECTIONSTRING_NAME"),
                properties.getProperty("AZURE_STORAGEACCOUNT_CONNECTIONSTRING_VERSION"),
                properties.getProperty("AZURE_CONNECTION_STRING")
        );
        return config;
    }
}