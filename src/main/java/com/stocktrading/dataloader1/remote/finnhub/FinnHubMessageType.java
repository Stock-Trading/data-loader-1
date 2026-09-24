package com.stocktrading.dataloader1.remote.finnhub;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
enum FinnHubMessageType {

    PING("ping"),
    PONG("pong"),
    SUBSCRIBE("subscribe"),
    UNSUBSCRIBE("unsubscribe");

    private final String messageType;

}
