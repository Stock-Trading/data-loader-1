package com.stocktrading.dataloader1.remote.subbscriptionmanager;

import lombok.Builder;

import java.time.Instant;

@Builder
record DataLoaderDto(String uuid,
                     Instant checkedIn) {
}
