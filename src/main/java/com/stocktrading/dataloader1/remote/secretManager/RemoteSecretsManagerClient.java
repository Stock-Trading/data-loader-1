package com.stocktrading.dataloader1.remote.secretManager;

interface RemoteSecretsManagerClient {

    String getSecret(String secretName, String secretKey);

}
