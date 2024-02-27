package com.stocktrading.dataloader1.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record TimeSeriesDaily(
        @JsonProperty("Meta Data") MetaData metaData,
        @JsonProperty("Time Series (Daily)") Map<String, DayParameters> dayParametersMap
) {}
