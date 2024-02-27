package com.stocktrading.dataloader1.config;

import software.amazon.awssdk.regions.Region;

public interface RemoteSecretsManagerClient {

    String getSecretFromAws(String secretName, Region awsRegion);

}
