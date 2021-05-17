package com.cymetrics.storage.implementation.configs;

public class AzureBlobConfig {
    private String TENANT_ID;
    private String CLIENT_ID;
    private String CLIENT_SECRET;
    private String KEYVAULT_URL;
    private String STORAGEACCOUNT_CONNECTIONSTRING_NAME;
    private String STORAGEACCOUNT_CONNECTIONSPRING_VERSION;
    private String CONNECTION_STRING;
    public AzureBlobConfig(String TENANT_ID, String CLIENT_ID, String CLIENT_SECRET, String KEYVAULT_URL, String STORAGEACCOUNT_CONNECTIONSTRING_NAME, String STORAGEACCOUNT_CONNECTIONSPRING_VERSION, String CONNECTION_STRING) {
        this.TENANT_ID = TENANT_ID;
        this.CLIENT_ID = CLIENT_ID;
        this.CLIENT_SECRET = CLIENT_SECRET;
        this.KEYVAULT_URL = KEYVAULT_URL;
        this.STORAGEACCOUNT_CONNECTIONSTRING_NAME = STORAGEACCOUNT_CONNECTIONSTRING_NAME;
        this.STORAGEACCOUNT_CONNECTIONSPRING_VERSION = STORAGEACCOUNT_CONNECTIONSPRING_VERSION;
        this.CONNECTION_STRING = CONNECTION_STRING;
    }

    public String getTENANT_ID() {
        return TENANT_ID;
    }

    public String getCLIENT_ID() {
        return CLIENT_ID;
    }

    public String getCLIENT_SECRET() {
        return CLIENT_SECRET;
    }

    public String getKEYVAULT_URL() {
        return KEYVAULT_URL;
    }

    public String getSTORAGEACCOUNT_CONNECTIONSTRING_NAME() {
        return STORAGEACCOUNT_CONNECTIONSTRING_NAME;
    }

    public String getSTORAGEACCOUNT_CONNECTIONSPRING_VERSION() {
        return STORAGEACCOUNT_CONNECTIONSPRING_VERSION;
    }

    public String getCONNECTION_STRING() {
        return CONNECTION_STRING;
    }
}
