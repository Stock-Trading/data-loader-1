package com.stocktrading.dataloader1.remote.finnHub;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FinnHubMessageType {

    PING("ping"),
    PONG("pong"),
    SUBSCRIBE("subscribe"),
    UNSUBSCRIBE("unsubscribe");

    private final String messageType;

}
