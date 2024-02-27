package com.stocktrading.dataloader1.remoteClient.secretManager;

import software.amazon.awssdk.regions.Region;

interface RemoteSecretsManagerClient {

    String getSecretFromAws(String secretName, Region awsRegion);

}
