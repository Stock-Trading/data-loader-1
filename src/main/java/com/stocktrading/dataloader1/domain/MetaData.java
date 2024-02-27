package com.stocktrading.dataloader1.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public record MetaData(
        @JsonProperty("1. Information") String information,
        @JsonProperty("2. Symbol") String symbol,
        @JsonProperty("3. Last Refreshed") Date lastRefreshed,
        @JsonProperty("4. Output Size") String outputSize,
        @JsonProperty("5. Time Zone") String timeZone
) {}
