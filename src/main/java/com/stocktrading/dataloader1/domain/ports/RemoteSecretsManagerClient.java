package com.stocktrading.dataloader1.domain.ports;

import java.util.List;

public interface RemoteSecretsManagerClient {

    List<String> getFinnHubApiKeys();

}
