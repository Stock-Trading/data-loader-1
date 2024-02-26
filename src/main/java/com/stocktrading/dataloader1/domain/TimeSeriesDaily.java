package com.stocktrading.dataloader1.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class TimeSeriesDaily {

    @JsonProperty("Meta Data")
    MetaData metaData;
    @JsonProperty("Time Series (Daily)")
    Map<String, DayParameters> dayParametersMap;

}
