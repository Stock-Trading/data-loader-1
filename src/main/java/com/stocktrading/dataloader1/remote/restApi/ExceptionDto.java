package com.stocktrading.dataloader1.remote.restApi;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
record ExceptionDto(String message,
                    LocalDateTime timeStamp) {
}
