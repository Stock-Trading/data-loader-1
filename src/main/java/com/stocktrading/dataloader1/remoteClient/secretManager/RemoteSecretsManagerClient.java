package com.stocktrading.dataloader1.remoteClient.secretManager;

interface RemoteSecretsManagerClient {

    String getSecret(String secretName, String secretKey);

}
