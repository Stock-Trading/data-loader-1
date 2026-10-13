package com.stocktrading.dataloader1.domain.service;

import com.stocktrading.dataloader1.domain.ports.SubscriptionManagerClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DataLoaderServiceTest {

    @Mock
    private SubscriptionManagerClient subscriptionManagerClient;

    private DataLoaderService service;

    @Test
    @DisplayName("""
            given Subscription Manager is reachable,
            when registerDataLoaderToSubscriptionManagerOnStartup is called,
            then it should register and not retry on the next scheduled attempt
            """)
    void should() {
        // given
        service = new DataLoaderService(subscriptionManagerClient);

        // when
        service.registerDataLoaderToSubscriptionManagerOnStartup();
        service.retryRegistrationToSubscriptionManagerIfNeeded();

        // then
        verify(subscriptionManagerClient, times(1)).registerToSubscriptionManager();
    }

    @Test
    @DisplayName("""
            given Subscription Manager is offline,
            when registerDataLoaderToSubscriptionManagerOnStartup is called,
            then it should log and swallow the exception instead of propagating it
            """)
    void should2() {
        // given
        service = new DataLoaderService(subscriptionManagerClient);
        willThrow(new RuntimeException("connection refused")).given(subscriptionManagerClient).registerToSubscriptionManager();

        // when
        // then
        service.registerDataLoaderToSubscriptionManagerOnStartup();
    }

    @Test
    @DisplayName("""
            given registration failed on startup,
            when retryRegistrationToSubscriptionManagerIfNeeded is called,
            then it should attempt registration again
            """)
    void should3() {
        // given
        service = new DataLoaderService(subscriptionManagerClient);
        willThrow(new RuntimeException("connection refused")).given(subscriptionManagerClient).registerToSubscriptionManager();
        service.registerDataLoaderToSubscriptionManagerOnStartup();

        // when
        service.retryRegistrationToSubscriptionManagerIfNeeded();

        // then
        verify(subscriptionManagerClient, times(2)).registerToSubscriptionManager();
    }

    @Test
    @DisplayName("""
            given Subscription Manager check-in call fails,
            when checkInDataLoaderToSubscriptionManager is called,
            then it should log and swallow the exception instead of propagating it
            """)
    void should4() {
        // given
        service = new DataLoaderService(subscriptionManagerClient);
        willThrow(new RuntimeException("connection refused")).given(subscriptionManagerClient).checkInToSubscriptionManager();

        // when
        // then
        service.checkInDataLoaderToSubscriptionManager();
        verify(subscriptionManagerClient, never()).registerToSubscriptionManager();
    }
}
