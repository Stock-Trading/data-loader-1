package com.stocktrading.dataloader1.remote.subbscriptionmanager;

import com.stocktrading.dataloader1.domain.model.DataLoaderModel;
import com.stocktrading.dataloader1.domain.model.SubscriptionModel;
import com.stocktrading.dataloader1.domain.ports.SubscriptionManagerClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class SubscriptionManagerClientService implements SubscriptionManagerClient {

    private final SubscriptionManagerRestClient restClient;
    private final DataLoaderModel dataLoaderModel;
    private final DataLoaderMapper mapper;

    public SubscriptionManagerClientService(SubscriptionManagerRestClient restClient, DataLoaderModel dataLoaderModel, DataLoaderMapper mapper) {
        this.restClient = restClient;
        this.dataLoaderModel = dataLoaderModel;
        this.mapper = mapper;
    }

    @Override
    public void registerToSubscriptionManager() {
        DataLoaderDto requestDto = mapper.mapToDto(dataLoaderModel);
        ResponseEntity<DataLoaderDto> response = restClient.subscriptionManagerSimpleRestClient()
                .post()
                .uri("http://localhost:8080/api/v1/internal/dataLoader/" + requestDto.uuid())
                .retrieve()
                .toEntity(DataLoaderDto.class);
        var responseDto = response.getBody();
        log.debug("Response status in registerToSubscriptionManager(): {}", response.getStatusCode());
        log.info("Successfully registered Data Loader in Subscription Manager under uuid {}. Checked in at {}",
                responseDto.uuid(),
                responseDto.checkedIn());
    }

    @Override
    public void checkInToSubscriptionManager() {
        DataLoaderDto requestDto = mapper.mapToDto(dataLoaderModel);
        ResponseEntity<DataLoaderDto> response = restClient.subscriptionManagerSimpleRestClient()
                .put()
                .uri("http://localhost:8080/api/v1/internal/dataLoader/" + requestDto.uuid() + "/last-connection-time")
                .retrieve()
                .toEntity(DataLoaderDto.class);
        var responseDto = response.getBody();
        log.debug("Response status in checkInToSubscriptionManager(): {}", response.getStatusCode());
        log.debug("Successfully checked-in Data Loader in Subscription Manager at {}.",
                responseDto.checkedIn());
    }

    @Override
    public SubscriptionModel getSubscription() {
        ResponseEntity<SubscriptionDto> response = restClient.subscriptionManagerSimpleRestClient()
                .get()
                .uri("http://localhost:8080/api/v1/internal/subscription/" + dataLoaderModel.getUuid())
                .retrieve()
                .toEntity(SubscriptionDto.class);

        SubscriptionDto subscriptionDto = response.getBody();
        log.debug("Response status in getSubscription: {}", response.getStatusCode());
        validateDataLoaderUuid(subscriptionDto);
        return mapper.mapToModel(subscriptionDto);
    }

    private void validateDataLoaderUuid(SubscriptionDto subscriptionDto) {
        if (!subscriptionDto.dataLoaderUuid().equals(dataLoaderModel.getUuid())){
            throw new RuntimeException("Validation of Data Loader UUID failed! Received in response: " + subscriptionDto.dataLoaderUuid() +
                    ", actual UUID of this instance of Data Loader" + dataLoaderModel.getUuid());
        }
    }
    
}
