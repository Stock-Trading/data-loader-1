package com.stocktrading.dataloader1.remote.restapi;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
record ExceptionDto(String message,
                    LocalDateTime timeStamp) {
}
