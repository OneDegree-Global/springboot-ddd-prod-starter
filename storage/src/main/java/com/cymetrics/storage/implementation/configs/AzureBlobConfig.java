package com.cymetrics.storage.implementation.configs;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AzureBlobConfig {
    private String TENANT_ID;
    private String CLIENT_ID;
    private String CLIENT_SECRET;
    private String KEYVAULT_URL;
    private String STORAGEACCOUNT_CONNECTIONSTRING_NAME;
    private String STORAGEACCOUNT_CONNECTIONSPRING_VERSION;
    private String CONNECTION_STRING;
}
